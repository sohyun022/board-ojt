package org.ojt.board_ojt.api.auth.controller;

import lombok.RequiredArgsConstructor;
import org.ojt.board_ojt.api.auth.dto.req.LoginReq;

import org.ojt.board_ojt.api.auth.dto.res.Token;
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

        Token token = authService.login(loginReq);

        return ResponseEntity.ok(token);
    }


}
