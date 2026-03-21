package com.iisovaii.client_bff.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iisovaii.client_bff.dto.error.ErrorResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
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

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null) {
            try {
                Claims claims = jwtValidator.validate(token);

                UUID userId = UUID.fromString(claims.getSubject());
                List<String> roles = extractRoles(claims);

                List<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userId, token, authorities);
                auth.setDetails(claims);

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JwtExpiredException e) {
                SecurityContextHolder.clearContext();
                writeUnauthorized(response, "TOKEN_EXPIRED", e.getMessage());
                return;
            } catch (JwtInvalidException e) {
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
            case String s -> {
                if (s.isBlank()) return List.of();
                return List.of(s);
            }
            case Collection<?> coll -> {
                List<String> roles = new ArrayList<>(coll.size());
                for (Object item : coll) {
                    if (item == null) continue;
                    String role = item.toString();
                    if (!role.isBlank()) roles.add(role);
                }
                return roles;
            }
            default -> {
            }
        }

        // неизвестный формат — не валим запрос, просто без ролей
        return List.of();
    }

    private void writeUnauthorized(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(code, message, LocalDateTime.now()));
    }

    private String extractToken(HttpServletRequest request) {
        // токен приходит либо в заголовке, либо в httpOnly cookie
        // для BFF используем cookie — Angular его не видит, браузер сам подставляет
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // fallback на Authorization header (для межсервисных вызовов)
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // пропускаем публичные эндпоинты без проверки
        return path.startsWith("/bff/client/auth/") || path.startsWith("/ws/");
    }
}
