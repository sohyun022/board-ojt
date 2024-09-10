package org.ojt.board_ojt.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // SLF4J Logger 생성 -> 왜 씀? -> 이벤트 기록, 경고, 오류, 디버그
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        try {
            // 1. JWT 토큰을 요청에서 추출
            String token = getJwtFromRequest(request);

            // 2. 토큰이 존재하고 유효한지 확인
            if (token != null && jwtUtil.validateToken(token)) {
                // 토큰이 유효한 경우 인증 객체 생성
                Authentication authentication = jwtUtil.getAuthentication(token);

                // SecurityContext 에 인증 객체 설정 -> security context 가 뭐지?
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 인증이 성공했음을 정보 로그로 기록
                logger.info("Successfully authenticated user: {}", authentication.getName());
            }

        } catch (Exception ex) {
            // 예외 발생 시 에러 로그로 기록
            logger.error("Failed to authenticate user", ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return; // 필터 체인 종료
        }

        // 3. 필터 체인을 계속 진행
        chain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거하고 JWT 토큰만 반환
        }
        return null;
    }

}