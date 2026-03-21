package com.iisovaii.employee_bff.service;

import com.iisovaii.employee_bff.client.SsoServiceClient;
import com.iisovaii.employee_bff.dto.auth.LoginUrlResponse;
import com.iisovaii.employee_bff.dto.response.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final SsoServiceClient ssoServiceClient;

    @Value("${sso.authorization-url}")
    private String authorizationUrl;

    @Value("${sso.client-id}")
    private String clientId;

    @Value("${sso.redirect-uri}")
    private String redirectUri;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.cookie-name}")
    private String cookieName;

    public LoginUrlResponse buildLoginUrl() {
        String url = UriComponentsBuilder
                .fromUri(URI.create(authorizationUrl))
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "openid profile roles")
                .queryParam("state", generateState())
                .build()
                .toUriString();

        return new LoginUrlResponse(url);
    }

    public void exchangeCodeAndSetCookie(
            String code,
            HttpServletResponse response) {

        TokenResponse tokenResponse = ssoServiceClient.exchangeCode(
                "authorization_code",
                code,
                redirectUri,
                clientId
        );

        // используем геттеры а не record-стиль
        Cookie cookie = new Cookie(cookieName, tokenResponse.getAccessToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) tokenResponse.getExpiresIn());
        // cookie.setSecure(true); // раскомментировать в проде

        response.addCookie(cookie);
    }

    public void logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String generateState() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}