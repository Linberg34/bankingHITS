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
import org.springframework.transaction.annotation.Transactional;
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
    private final UserProvisioningService userProvisioningService;

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
    @Transactional
    public void register(
            String name,
            String username,
            String password,
            List<Role> roles) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Пользователь уже существует"
            );
        }

        List<Role> effectiveRoles = normalizeRoles(roles);
        SsoUser user = SsoUser.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .roles(effectiveRoles)
                .status(SsoUser.UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
        userProvisioningService.ensureUserProfile(
                resolveDisplayName(name, username),
                username,
                effectiveRoles
        );
    }

    public void ensureRegistered(
            String name,
            String username,
            String password,
            List<Role> roles) {
        if (userRepository.findByUsername(username).isPresent()) {
            userProvisioningService.ensureUserProfile(
                    resolveDisplayName(name, username),
                    username,
                    normalizeRoles(roles)
            );
            return;
        }

        register(name, username, password, roles);
    }

    public Map<String, Object> getJwks() {
        return jwkSet.toPublicJWKSet().toJSONObject();
    }

    private List<Role> normalizeRoles(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return List.of(Role.CLIENT);
        }
        return roles;
    }

    private String resolveDisplayName(String name, String username) {
        if (name != null && !name.isBlank()) {
            return name;
        }
        return username;
    }
}
