package com.gautama.bankhitscredit.infrastructure.http;

import com.gautama.bankhitscredit.infrastructure.trace.TraceContextHolder;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class TraceRequestFilter extends OncePerRequestFilter {

    private final String defaultAppSource;

    public TraceRequestFilter(
            @Value("${monitoring.app:system}") String defaultAppSource
    ) {
        this.defaultAppSource = defaultAppSource;
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
            logCompleted(request, response.getStatus(), startedAt, traceId, null);
        } catch (Exception exception) {
            logCompleted(request, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, startedAt, traceId, exception);
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
    }

    private String resolveHeader(HttpServletRequest request, String headerName, String fallback) {
        String headerValue = request.getHeader(headerName);
        return StringUtils.hasText(headerValue) ? headerValue : fallback;
    }
}
