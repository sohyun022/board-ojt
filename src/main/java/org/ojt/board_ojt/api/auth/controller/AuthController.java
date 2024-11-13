package org.ojt.board_ojt.api.auth.controller;

import lombok.RequiredArgsConstructor;
import org.ojt.board_ojt.api.auth.dto.req.LoginReq;

import org.ojt.board_ojt.api.auth.dto.res.TokenRes;
import org.ojt.board_ojt.api.auth.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq loginReq) {
        TokenRes tokenRes = authService.login(loginReq);
        return ResponseEntity.ok(tokenRes);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshAccessToken(@RequestHeader("Authorization") String authorizationHeader) {
        // "Bearer " 접두사를 제거하고 토큰만 추출
        String refreshToken = authorizationHeader.replace("Bearer ", "");
        // 액세스 토큰 재발급 시도
        String newAccessToken = authService.reissuedAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken); // 성공 시 200 OK 응답
    }


}
