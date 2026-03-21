package com.iisovaii.sso_service.dto;

import com.iisovaii.sso_service.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Schema(
            description = "Роли пользователя",
            allowableValues = {"CLIENT", "EMPLOYEE"}
    )
    private List<Role> roles;
}