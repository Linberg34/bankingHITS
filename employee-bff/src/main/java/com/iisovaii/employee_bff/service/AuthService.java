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

    @Value("${sso.base-url}")
    private String ssoBaseUrl;

    public LoginUrlResponse buildLoginUrl() {
        return new LoginUrlResponse(ssoBaseUrl + "/auth/login");
    }
}