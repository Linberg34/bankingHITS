package com.gautama.bankhitsuser.service;

import com.gautama.bankhitsuser.config.JwtUtil;
import com.gautama.bankhitsuser.dto.RegisterDTO;
import com.gautama.bankhitsuser.dto.TokenDTO;
import com.gautama.bankhitsuser.enums.Role;
import com.gautama.bankhitsuser.model.RevokedToken;
import com.gautama.bankhitsuser.model.User;
import com.gautama.bankhitsuser.repository.RevokedTokenRepository;
import com.gautama.bankhitsuser.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RevokedTokenRepository revokedTokenRepository;
    private final JwtUtil jwtUtil;

    public TokenDTO register(RegisterDTO request, Role role) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadCredentialsException("Пользователь уже существует.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(role);

        userRepository.save(user);
        return new TokenDTO(jwtUtil.generateToken(user));
    }

    public TokenDTO login(String email) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, "password"));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("Пользователь не найден."));
        return new TokenDTO(jwtUtil.generateToken(user));
    }


    public void logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Неверный токен.");
        }

        String token = authHeader.substring(7);
        RevokedToken revokedToken = new RevokedToken();
        revokedToken.setToken(token);
        revokedTokenRepository.save(revokedToken);
    }

}
