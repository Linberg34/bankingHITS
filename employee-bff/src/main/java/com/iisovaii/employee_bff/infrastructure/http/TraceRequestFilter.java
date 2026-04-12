package com.iisovaii.employee_bff.infrastructure.http;

import com.iisovaii.employee_bff.infrastructure.trace.TraceContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class TraceRequestFilter extends OncePerRequestFilter {

    private static final String SERVICE_NAME = "employee-bff";

    private final String defaultAppSource;
    private final RestClient monitoringClient;

    public TraceRequestFilter(
            @Value("${monitoring.app:system}") String defaultAppSource,
            RestClient.Builder restClientBuilder,
            @Value("${monitoring.service-url:http://localhost:8087}") String monitoringServiceUrl
    ) {
        this.defaultAppSource = defaultAppSource;
        this.monitoringClient = restClientBuilder.baseUrl(monitoringServiceUrl).build();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String traceId = resolveHeader(request, TraceContextHolder.TRACE_HEADER, TraceContextHolder.generateTraceId());
        String appSource = resolveHeader(request, TraceContextHolder.APP_HEADER, defaultAppSource);

        TraceContextHolder.set(traceId, appSource);
        response.setHeader(TraceContextHolder.TRACE_HEADER, traceId);
        response.setHeader(TraceContextHolder.APP_HEADER, appSource);

        long startedAt = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
            logCompleted(request, response.getStatus(), startedAt, traceId, appSource, null);
        } catch (Exception exception) {
            logCompleted(request, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, startedAt, traceId, appSource, exception);
            throw exception;
        } finally {
            TraceContextHolder.clear();
        }
    }

    private void logCompleted(
            HttpServletRequest request,
            int status,
            long startedAt,
            String traceId,
            String appSource,
            Exception exception
    ) {
        long latencyMs = System.currentTimeMillis() - startedAt;
        String message = String.format(
                "[trace] %s %s status=%d latencyMs=%d traceId=%s",
                request.getMethod(),
                request.getRequestURI(),
                status,
                latencyMs,
                traceId
        );

        if (exception != null || status >= 500) {
            log.error(message, exception);
        } else if (status >= 400) {
            log.warn(message);
        } else {
            log.info(message);
        }

        publishToMonitoring(request.getMethod(), request.getRequestURI(), status, latencyMs, traceId, appSource);
    }

    private void publishToMonitoring(String method, String path, int status, long latencyMs, String traceId, String appSource) {
        String level = status >= 500 ? "error" : status >= 400 ? "warn" : "info";
        String msg = status >= 500 ? "Ошибка сервера" : "Запрос выполнен";
        MonitoringPayload payload = new MonitoringPayload(
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                appSource,
                SERVICE_NAME,
                level,
                method,
                path,
                status,
                latencyMs,
                0,
                false,
                "CLOSED",
                traceId,
                msg
        );

        try {
            monitoringClient.post()
                    .uri("/api/monitoring/logs")
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            log.warn("Failed to publish monitoring event: {}", ex.getMessage());
        }
    }

    private String resolveHeader(HttpServletRequest request, String headerName, String fallback) {
        String headerValue = request.getHeader(headerName);
        return StringUtils.hasText(headerValue) ? headerValue : fallback;
    }

    private record MonitoringPayload(
            String id,
            long timestamp,
            String app,
            String service,
            String level,
            String method,
            String path,
            int status,
            long latencyMs,
            int retries,
            boolean blockedByCircuit,
            String circuitState,
            String traceId,
            String message
    ) {
    }
}
