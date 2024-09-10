package org.ojt.board_ojt.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.ojt.board_ojt.api.auth.domain.RefreshToken;
import org.ojt.board_ojt.api.auth.repository.RefreshTokenRepository;
import org.ojt.board_ojt.api.member.domain.Member;

import org.ojt.board_ojt.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    private final CustomUserDetailsService userDetailsService;

    private final RefreshTokenRepository refreshTokenRepository; // 주입 받아 사용

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

        try {
            return Jwts.builder()
                    .setSubject(member.getEmail())
                    .claim("memberId", member.getMemberId())
                    .claim("name", member.getName())
                    .setExpiration(expiration)
                    .setIssuedAt(now)
                    .signWith(key, signatureAlgorithm)
                    .compact();
        } catch (JwtException e) {
            throw new JwtException("토큰 error.");
        }
    }

    public Authentication getAuthentication(String token) {
        String email = getClaims(token).getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        System.out.println("userDetails = " + userDetails);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public String refreshTokenValidation(String token) {
        String email = getClaims(token).getSubject();
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(email);
        boolean isRefreshTokenValidation = refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());

        if (!isRefreshTokenValidation) {
            throw new JwtException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 리프레시 토큰의 만료 시간 확인
        Claims claims = getClaims(token);
        Date expirationDate = claims.getExpiration();
        Date now = new Date();

        if (expirationDate.before(now)) {
            throw new JwtException("리프레시 토큰이 만료되었습니다.");
        }

        // 리프레시 토큰이 유효하고 만료되지 않은 경우 이메일 반환
        return email;
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true; // 토큰이 유효함
        } catch (JwtException | IllegalArgumentException e) {
            return false; // 토큰이 유효하지 않음
        }
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("토큰 error.");
        }
    }
}