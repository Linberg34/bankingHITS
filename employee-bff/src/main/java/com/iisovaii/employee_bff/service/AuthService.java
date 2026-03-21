package com.iisovaii.employee_bff.service;

import com.iisovaii.employee_bff.dto.auth.LoginUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    @Value("${sso.base-url}")
    private String ssoBaseUrl;

    public LoginUrlResponse buildLoginUrl() {
        // просто отдаём Angular адрес SSO для логина
        return new LoginUrlResponse(ssoBaseUrl + "/auth/login");
    }
}