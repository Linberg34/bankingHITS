package com.iisovaii.employee_bff.config;

import com.iisovaii.employee_bff.client.SsoServiceClient;
import com.iisovaii.employee_bff.dto.SsoRegisterRequest;
import com.iisovaii.employee_bff.dto.response.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "docker"})
@Slf4j
public class DevConfig {

    @Bean
    @Primary  // перекрывает реальный Feign клиент
    public SsoServiceClient mockSsoServiceClient() {
        return new SsoServiceClient() {

            @Override
            public TokenResponse exchangeCode(
                    String grantType,
                    String code,
                    String redirectUri,
                    String clientId) {
                log.info("[DEV] SsoServiceClient.exchangeCode вызван");
                return new TokenResponse(
                        "dev-mock-token",
                        "dev-mock-refresh",
                        3600L,
                        "Bearer"
                );
            }

            @Override
            public void register(SsoRegisterRequest request) {
                log.info(
                        "[DEV] SsoServiceClient.register: {}",
                        request.getUsername()
                );
                // просто ничего не делаем
            }
        };
    }
}