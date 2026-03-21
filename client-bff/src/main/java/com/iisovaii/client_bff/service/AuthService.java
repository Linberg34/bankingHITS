package com.iisovaii.client_bff.service;

import com.iisovaii.client_bff.client.SsoServiceClient;
import com.iisovaii.client_bff.dto.auth.LoginUrlResponse;
import com.iisovaii.client_bff.mapper.auth.AuthMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SsoServiceClient ssoServiceClient;
    private final AuthMapper authMapper;

    @Value("${spring.security.oauth2.client.registration.sso.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.sso.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.sso.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.sso.scope}")
    private String scope;

    @Value("${sso.auth-url:}")
    private String authUrlProperty;

    /**
     * Строит ссылку для редиректа на SSO /authorize endpoint.
     */
    public LoginUrlResponse buildLoginUrl() {
        String authorizationEndpoint = resolveAuthorizationEndpoint();

        StringBuilder url = new StringBuilder(authorizationEndpoint);
        if (!authorizationEndpoint.contains("?")) {
            url.append("?");
        } else if (!authorizationEndpoint.endsWith("&") && !authorizationEndpoint.endsWith("?")) {
            url.append("&");
        }

        url.append("response_type=code");
        url.append("&client_id=").append(encode(clientId));
        url.append("&redirect_uri=").append(encode(redirectUri));

        if (scope != null && !scope.isBlank()) {
            url.append("&scope=").append(encode(scope));
        }

        // state можно добавить позже на фронте / через отдельный сервис

        return authMapper.toLoginUrlResponse(url.toString());
    }

    /**
     * Обменивает authorization code на access_token у SSO и кладёт его в httpOnly cookie.
     */
    public void exchangeCodeAndSetCookie(String code, HttpServletResponse response) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        Map<String, Object> tokenResponse = ssoServiceClient.exchangeCode(body);

        String accessToken = Optional.ofNullable(tokenResponse.get("access_token"))
                .map(Object::toString)
                .orElseThrow(() -> new IllegalStateException("SSO response does not contain access_token"));

        Cookie cookie = new Cookie("access_token", accessToken);
        cookie.setHttpOnly(true);
        // В dev-режиме можно оставить secure=false, в проде лучше включить
        cookie.setSecure(false);
        cookie.setPath("/");
        // maxAge = -1 означает cookie живёт до закрытия браузера; можно читать expires_in и настраивать отдельно
        cookie.setMaxAge(-1);

        response.addCookie(cookie);
    }

    /**
     * Очищает access_token cookie (logout на стороне BFF).
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    Cookie cleared = new Cookie("access_token", "");
                    cleared.setHttpOnly(true);
                    cleared.setSecure(cookie.getSecure());
                    cleared.setPath(cookie.getPath() != null ? cookie.getPath() : "/");
                    cleared.setMaxAge(0);
                    response.addCookie(cleared);
                    return;
                }
            }
        }

        // Если cookie не было — всё равно выставим "пустую" cookie с maxAge=0
        Cookie cleared = new Cookie("access_token", "");
        cleared.setHttpOnly(true);
        cleared.setSecure(false);
        cleared.setPath("/");
        cleared.setMaxAge(0);
        response.addCookie(cleared);
    }

    private String resolveAuthorizationEndpoint() {
        if (authUrlProperty != null && !authUrlProperty.isBlank()) {
            return authUrlProperty;
        }
        // fallback: пытаемся вывести /authorize из token-url (например, /auth/token -> /auth/authorize)
        // sso.token-url задаётся как полный URL, поэтому заменяем только последний сегмент
        String tokenUrl = System.getProperty("sso.token-url");
        if (tokenUrl == null || tokenUrl.isBlank()) {
            // как крайний случай — пусть будет redirectUri домен, но без гарантий
            return redirectUri;
        }
        int lastSlash = tokenUrl.lastIndexOf('/');
        if (lastSlash <= 0) {
            return tokenUrl;
        }
        return tokenUrl.substring(0, lastSlash + 1) + "authorize";
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

