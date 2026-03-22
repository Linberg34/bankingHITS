package com.iisovaii.employee_bff.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignConfig {

    // прокидывает JWT из SecurityContext во все исходящие Feign запросы
    @Bean
    public RequestInterceptor jwtRelayInterceptor() {
        return requestTemplate -> {
            Authentication auth =
                    SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.getCredentials() instanceof String token) {
                requestTemplate.header(
                        HttpHeaders.AUTHORIZATION, "Bearer " + token
                );
            }
        };
    }
}
