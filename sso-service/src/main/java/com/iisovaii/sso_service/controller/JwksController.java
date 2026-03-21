package com.iisovaii.sso_service.controller;

import com.iisovaii.sso_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksController {

    private final AuthService authService;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> jwks() {
        return authService.getJwks();
    }
}