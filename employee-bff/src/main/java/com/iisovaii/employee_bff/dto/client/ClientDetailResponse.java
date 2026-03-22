package com.iisovaii.employee_bff.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDetailResponse {
    private UUID userId;
    private String name;
    private String email;
    private String status;
    private LocalDateTime registeredAt;
}
