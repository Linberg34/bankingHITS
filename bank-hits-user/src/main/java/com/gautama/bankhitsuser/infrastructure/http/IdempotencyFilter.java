package com.gautama.bankhitsuser.infrastructure.http;

import com.gautama.bankhitsuser.infrastructure.trace.TraceContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class IdempotencyFilter extends OncePerRequestFilter {

    private static final Set<String> IDEMPOTENT_METHODS = Set.of(
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name()
    );

    private final Map<String, CachedHttpResponse> cache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!IDEMPOTENT_METHODS.contains(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String idempotencyKey = request.getHeader(TraceContextHolder.IDEMPOTENCY_HEADER);
        if (!StringUtils.hasText(idempotencyKey)) {
            filterChain.doFilter(request, response);
            return;
        }

        String cacheKey = request.getMethod() + ":" + request.getRequestURI() + ":" + idempotencyKey;
        CachedHttpResponse cachedResponse = cache.get(cacheKey);
        if (cachedResponse != null) {
            writeCachedResponse(response, cachedResponse);
            return;
        }

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(request, responseWrapper);

        if (responseWrapper.getStatus() < HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            cache.putIfAbsent(cacheKey, capture(responseWrapper));
        }

        responseWrapper.copyBodyToResponse();
    }

    private CachedHttpResponse capture(ContentCachingResponseWrapper response) {
        Map<String, List<String>> headers = response.getHeaderNames()
                .stream()
                .collect(ConcurrentHashMap::new, (map, headerName) -> map.put(headerName, List.copyOf(response.getHeaders(headerName))), Map::putAll);

        return new CachedHttpResponse(
                response.getStatus(),
                headers,
                response.getContentAsByteArray()
        );
    }

    private void writeCachedResponse(
            HttpServletResponse response,
            CachedHttpResponse cachedResponse
    ) throws IOException {
        response.setStatus(cachedResponse.status());
        cachedResponse.headers().forEach((headerName, values) -> {
            if (!values.isEmpty()) {
                response.setHeader(headerName, values.get(0));
                for (int index = 1; index < values.size(); index++) {
                    response.addHeader(headerName, values.get(index));
                }
            }
        });

        if (cachedResponse.body().length > 0) {
            response.getOutputStream().write(cachedResponse.body());
        } else {
            response.getWriter().write("");
        }
    }

    private record CachedHttpResponse(
            int status,
            Map<String, List<String>> headers,
            byte[] body
    ) {
        private CachedHttpResponse {
            body = body == null ? new byte[0] : body.clone();
            headers = Map.copyOf(headers);
        }
    }
}
