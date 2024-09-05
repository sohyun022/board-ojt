package org.ojt.board_ojt.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ojt.board_ojt.CustomException;
import org.ojt.board_ojt.ErrorCode;
import org.ojt.board_ojt.api.auth.domain.RefreshToken;
import org.ojt.board_ojt.api.auth.dto.res.Token;
import org.ojt.board_ojt.api.auth.repository.RefreshTokenRepository;
import org.ojt.board_ojt.api.member.domain.Member;
import org.ojt.board_ojt.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final String HEADER_NAME = "Authorization";
    private static final String SCHEME = "Bearer";

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private SecretKey key;

    @Value("${jwt.access}")
    private long ACCESS_TIME; // 토큰 만료 시간

    @Value("${jwt.refresh}")
    private long REFRESH_TIME; // 토큰 만료 시간

    public static final String ACCESS = "Access";
    public static final String REFRESH = "Refresh";
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void initialize() {
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String createToken(Member member, String type) {
        Date now = new Date();
        long expirationMillis = type.equals(ACCESS) ? ACCESS_TIME : REFRESH_TIME;
        Date expiration = new Date(now.getTime() + expirationMillis);

        try{
            return Jwts.builder()
                    .setSubject(member.getEmail())
                    .claim("memberId", member.getMemberId())
                    .claim("name", member.getName())
                    .setExpiration(expiration)
                    .setIssuedAt(new Date())
                    .signWith(key, signatureAlgorithm)
                    .compact();
        } catch (JwtException e) {
            throw new JwtException("토큰 생성중 오류가 발생했습니다.");
        }
    }

    public Claims verify(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 토큰.");
        }
    }

    public Authentication getAuthentication(String token) {
        String email = verify(token).getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public String refreshTokenValidation(String token) {
        String email = verify(token).getSubject();

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(email);

        boolean isRefreshTokenValidation = refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());

        if (!isRefreshTokenValidation) {
            throw new CustomException(ErrorCode.NOT_MATCHING_REFRESH_TOKEN);
        }

        return email;
    }

}