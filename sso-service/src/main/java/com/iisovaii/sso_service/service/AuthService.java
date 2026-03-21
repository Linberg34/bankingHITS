package com.iisovaii.sso_service.service;

import com.iisovaii.sso_service.domain.Role;
import com.iisovaii.sso_service.domain.SsoUser;
import com.iisovaii.sso_service.dto.TokenResponse;
import com.iisovaii.sso_service.repository.SsoUserRepository;
import com.nimbusds.jose.jwk.JWKSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final SsoUserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final JWKSet jwkSet;

    public TokenResponse login(
            String username, String password) {

        SsoUser user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Неверный логин или пароль"
                        )
                );

        if (user.getStatus() == SsoUser.UserStatus.BLOCKED) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Пользователь заблокирован"
            );
        }

        if (!passwordEncoder.matches(
                password, user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Неверный логин или пароль"
            );
        }

        String token = tokenService.generateToken(user);

        return new TokenResponse(token, 3600L, "Bearer");
    }

    // service/AuthService.java — метод register
    public void register(
            String username,
            String password,
            List<Role> roles) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Пользователь уже существует"
            );
        }

        SsoUser user = SsoUser.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .roles(roles != null ? roles : List.of(Role.CLIENT))
                .status(SsoUser.UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
    }

    public Map<String, Object> getJwks() {
        return jwkSet.toPublicJWKSet().toJSONObject();
    }
}
