package com.iisovaii.employee_bff.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iisovaii.employee_bff.dto.error.ErrorResponse;
import com.iisovaii.employee_bff.exception.JwtExpiredException;
import com.iisovaii.employee_bff.exception.JwtInvalidException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;
    private final ObjectMapper objectMapper;
    @Value("${app.cookie-name:access_token}")
    private String cookieName;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);
        log.info("Token extracted: {}", token != null ? "present" : "null");

        if (token != null) {
            try {
                Claims claims = jwtValidator.validate(token);
                log.info("Token valid, userId: {}, roles: {}",
                        claims.getSubject(),
                        claims.get("roles"));
                UUID userId = UUID.fromString(claims.getSubject());

                List<SimpleGrantedAuthority> authorities = extractRoles(claims)
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                token,
                                authorities
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(auth);

            } catch (JwtExpiredException e) {
                log.warn("JWT rejected: {}", e.getMessage());
                SecurityContextHolder.clearContext();
                writeUnauthorized(response, "TOKEN_EXPIRED", e.getMessage());
                return;
            } catch (JwtInvalidException e) {
                log.warn("JWT rejected: {}", e.getMessage());
                SecurityContextHolder.clearContext();
                writeUnauthorized(response, "TOKEN_INVALID", e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private List<String> extractRoles(Claims claims) {
        Object raw = claims.get("roles");
        switch (raw) {
            case null -> {
                return List.of();
            }
            case String role -> {
                if (role.isBlank()) {
                    return List.of();
                }
                return List.of(role);
            }
            case Collection<?> roles -> {
                List<String> values = new ArrayList<>(roles.size());
                for (Object item : roles) {
                    if (item == null) {
                        continue;
                    }

                    String value = item.toString();
                    if (!value.isBlank()) {
                        values.add(value);
                    }
                }
                return values;
            }
            default -> {
                return List.of();
            }
        }
    }

    private void writeUnauthorized(
            HttpServletResponse response,
            String code,
            String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getWriter(),
                new ErrorResponse(code, message, LocalDateTime.now())
        );
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("Authorization header: {}", header);

        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/bff/employee/auth/")
                || path.startsWith("/ws/")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs/");
    }
}
