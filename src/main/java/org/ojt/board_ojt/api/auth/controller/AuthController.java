package org.ojt.board_ojt.api.auth.controller;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.ojt.board_ojt.api.auth.dto.req.LoginReq;

import org.ojt.board_ojt.api.auth.dto.res.Token;
import org.ojt.board_ojt.api.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq loginReq) {

        Token token = authService.login(loginReq);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshAccessToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // "Bearer " 접두사를 제거하고 토큰만 추출
            String refreshToken = authorizationHeader.replace("Bearer ", "");
            // 액세스 토큰 재발급 시도
            String newAccessToken = authService.reissuedAccessToken(refreshToken);
            return ResponseEntity.ok(newAccessToken); // 성공 시 200 OK 응답
        } catch (JwtException e) {
            // JWT 관련 예외 처리 (토큰 만료, 유효하지 않은 토큰 등)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()); // 401 Unauthorized
        } catch (Exception e) {
            // 일반적인 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다."); // 500 Internal Server Error
        }
    }


}
