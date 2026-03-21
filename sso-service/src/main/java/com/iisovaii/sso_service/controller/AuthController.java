package com.iisovaii.sso_service.controller;

import com.iisovaii.sso_service.dto.LoginRequest;
import com.iisovaii.sso_service.dto.RegisterRequest;
import com.iisovaii.sso_service.dto.TokenResponse;
import com.iisovaii.sso_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// controller/AuthController.java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(
                authService.login(
                        request.getUsername(),
                        request.getPassword()
                )
        );
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody @Valid RegisterRequest request) {
        authService.register(
                request.getUsername(),
                request.getPassword(),
                request.getRoles()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
