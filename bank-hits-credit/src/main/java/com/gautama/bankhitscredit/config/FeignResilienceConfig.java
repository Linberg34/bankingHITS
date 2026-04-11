package com.gautama.bankhitscredit.config;

import com.gautama.bankhitscredit.infrastructure.trace.TraceContextHolder;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignResilienceConfig {

    @Bean
    public RequestInterceptor traceRelayInterceptor() {
        return requestTemplate -> {
            relayHeader(requestTemplate, TraceContextHolder.TRACE_HEADER, TraceContextHolder.traceId());
            relayHeader(requestTemplate, TraceContextHolder.APP_HEADER, TraceContextHolder.appSource());

            String idempotencyKey = extractHeaderFromCurrentRequest(TraceContextHolder.IDEMPOTENCY_HEADER);
            if (idempotencyKey != null && !idempotencyKey.isBlank()) {
                relayHeader(requestTemplate, TraceContextHolder.IDEMPOTENCY_HEADER, idempotencyKey);
            }
        };
    }

    private String extractHeaderFromCurrentRequest(String headerName) {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
            return null;
        }

        HttpServletRequest request = attrs.getRequest();
        return request != null ? request.getHeader(headerName) : null;
    }

    private void relayHeader(feign.RequestTemplate requestTemplate, String headerName, String value) {
        if (value != null && !value.isBlank()) {
            requestTemplate.header(headerName, value);
        }
    }
}
