package com.gautama.bankhitsuser.dto;

import com.gautama.bankhitsuser.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Status status;
    private LocalDate registeredAt;
}