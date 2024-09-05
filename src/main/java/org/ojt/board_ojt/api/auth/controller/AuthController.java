package org.ojt.board_ojt.api.auth.controller;

import lombok.RequiredArgsConstructor;
import org.ojt.board_ojt.api.auth.dto.req.LoginReq;

import org.ojt.board_ojt.api.auth.dto.res.Token;
import org.ojt.board_ojt.api.auth.service.AuthService;
import org.ojt.board_ojt.jwt.JwtUtil;
import org.ojt.board_ojt.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq loginReq) {

        Token token = authService.login(loginReq);

        return ResponseEntity.ok("refresh: "+token.getRefreshToken()
                +"\naccess: "+token.getAccessToken());
    }


}
