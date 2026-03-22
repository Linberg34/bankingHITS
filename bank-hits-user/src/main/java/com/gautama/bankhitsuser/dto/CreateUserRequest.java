package com.gautama.bankhitsuser.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private UUID id;

    @NotBlank
    private String name;

    @NotBlank
    private String email;
}
