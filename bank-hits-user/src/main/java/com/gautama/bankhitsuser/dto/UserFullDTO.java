package com.gautama.bankhitsuser.dto;

import com.gautama.bankhitsuser.enums.Role;
import com.gautama.bankhitsuser.enums.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserFullDTO {
    private Long id;
    private String name;
    private String email;
    private Status status;
    private LocalDate registeredAt;
    private Role role;
}