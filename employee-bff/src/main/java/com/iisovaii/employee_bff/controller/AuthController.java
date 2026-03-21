// controller/AuthController.java
package com.iisovaii.employee_bff.controller;

import com.iisovaii.employee_bff.client.SsoServiceClient;
import com.iisovaii.employee_bff.dto.auth.LoginUrlResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bff/employee/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${sso.base-url}")
    private String ssoBaseUrl;

    @Value("${app.cookie-name:access_token}")
    private String cookieName;

    // Angular может запросить URL SSO если не хочет хардкодить его у себя
    @GetMapping("/login-url")
    public ResponseEntity<LoginUrlResponse> getLoginUrl() {
        return ResponseEntity.ok(
                new LoginUrlResponse(ssoBaseUrl + "/auth/login")
        );
    }

    // logout — просто чистим cookie если используем cookie
    // если Angular хранит токен в памяти — этот эндпоинт вообще не нужен
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}