package com.iisovaii.employee_bff.security;

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
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"!dev", "!docker"})
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;
    @Value("${app.cookie-name:access_token}")
    private String cookieName;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null) {
            try {
                Claims claims = jwtValidator.validate(token);
                UUID userId = UUID.fromString(claims.getSubject());

                Object rolesObj = claims.get("roles");
                List<SimpleGrantedAuthority> authorities;

                if (rolesObj instanceof List<?> rolesList) {
                    authorities = rolesList.stream()
                            .map(role -> new SimpleGrantedAuthority(
                                    role.toString())
                            )
                            .collect(Collectors.toList());
                } else {
                    authorities = List.of();
                }

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                token,
                                authorities
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(auth);

            } catch (JwtExpiredException | JwtInvalidException e) {
                log.warn("JWT rejected: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        // токен приходит в заголовке Authorization: Bearer <token>
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        // fallback на cookie если вдруг используется
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
        // auth эндпоинты не требуют токена
        return path.startsWith("/bff/employee/auth/")
                || path.startsWith("/ws/");
    }
}