package com.gautama.bankhitsuser.dto;

import com.gautama.bankhitsuser.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private Status status;
    private LocalDateTime registeredAt;
}