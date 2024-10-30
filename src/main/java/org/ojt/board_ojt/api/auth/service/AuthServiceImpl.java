package org.ojt.board_ojt.api.auth.service;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ojt.board_ojt.api.auth.domain.RefreshToken;
import org.ojt.board_ojt.api.auth.dto.req.LoginReq;
import org.ojt.board_ojt.api.auth.dto.res.TokenRes;
import org.ojt.board_ojt.api.auth.repository.RefreshTokenRepository;
import org.ojt.board_ojt.api.member.domain.Member;
import org.ojt.board_ojt.api.member.repository.MemberRepository;
import org.ojt.board_ojt.jwt.JwtAuthenticationFilter;
import org.ojt.board_ojt.jwt.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    @Transactional
    public TokenRes login(LoginReq loginReq){

        // 사용자 이름으로 사용자 검색
        Member member = memberRepository.findByEmail(loginReq.getEmail())
                .orElse(null);

        if (member == null) {
            // 사용자 없음 예외 처리
            throw new RuntimeException("사용자를 찾을 수 없습니다."); // 사용자 정의 예외 클래스로 교체
        }

        if(!checkPassword(loginReq.getPassword(),member.getPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다."); // 사용자 정의 예외 클래스로 교체
        }

        RefreshToken refreshToken = insertRefreshToken(member);
        String accessToken =jwtUtil.createToken(member,JwtUtil.ACCESS);


        return TokenRes.builder()
                .memberId(member.getMemberId())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    // 비밀번호 검증
    private boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Transactional
    @Override
    public RefreshToken insertRefreshToken(Member member) {
        String email = member.getEmail();
        String refreshToken = jwtUtil.createToken(member, JwtUtil.REFRESH);
        Optional<RefreshToken> getToken = refreshTokenRepository.findByEmail(email);

        if (getToken.isPresent()) {
            getToken.get().updateToken(refreshToken);
            refreshTokenRepository.save(getToken.get());
            return getToken.get();
        }
        else {
            RefreshToken userToken = RefreshToken.builder()
                    .token(refreshToken)
                    .email(email)
                    .build();

            return refreshTokenRepository.save(userToken);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public String reissuedAccessToken (String refreshToken) {

        try {
            // 리프레시 토큰 검증
            String email = jwtUtil.refreshTokenValidation(refreshToken);

            // 사용자 정보 조회
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new JwtException("사용자를 찾을 수 없습니다."));

            // 새로운 액세스 토큰 생성
            return jwtUtil.createToken(member, JwtUtil.ACCESS);
        } catch (JwtException ex) {
            // 예외 처리 및 로그 기록
            logger.error("Failed to reissue access token: {}", ex.getMessage());
            throw ex;
        }
    }

}
