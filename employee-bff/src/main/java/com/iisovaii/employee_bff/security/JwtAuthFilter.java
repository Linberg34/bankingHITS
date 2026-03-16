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
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;

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

                // явно кастуем каждый элемент в String
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
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/bff/employee/auth/")
                || path.startsWith("/ws/");
    }
}