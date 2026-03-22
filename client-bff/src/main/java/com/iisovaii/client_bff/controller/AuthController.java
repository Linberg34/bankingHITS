package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.auth.LoginUrlResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bff/client/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "SSO аутентификация клиента")
public class AuthController {

    @Value("${sso.base-url}")
    private String ssoBaseUrl;

    @Value("${app.cookie-name:access_token}")
    private String cookieName;

    @GetMapping("/login-url")
    @Operation(summary = "Получить URL для логина через SSO")
    public ResponseEntity<LoginUrlResponse> getLoginUrl() {
        return ResponseEntity.ok(
                new LoginUrlResponse(ssoBaseUrl + "/auth/login")
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout клиента")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }
}