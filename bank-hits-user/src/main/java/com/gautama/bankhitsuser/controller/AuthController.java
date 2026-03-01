package com.gautama.bankhitsuser.controller;

import com.gautama.bankhitsuser.dto.LoginDTO;
import com.gautama.bankhitsuser.dto.RegisterDTO;
import com.gautama.bankhitsuser.dto.TokenDTO;
import com.gautama.bankhitsuser.enums.Role;
import com.gautama.bankhitsuser.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<TokenDTO> register(@RequestBody @Valid RegisterDTO request) {
        return ResponseEntity.ok(authService.register(request, Role.CLIENT));
    }

    @PostMapping("/register/employee")
    public ResponseEntity<TokenDTO> registerEmployee(@RequestBody @Valid RegisterDTO request) {
        return ResponseEntity.ok(authService.register(request, Role.EMPLOYEE));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO request) {
        return ResponseEntity.ok(authService.login(request.getEmail()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok("User logged out successfully");
    }


}