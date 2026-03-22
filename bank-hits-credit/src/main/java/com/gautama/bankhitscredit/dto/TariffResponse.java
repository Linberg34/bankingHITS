package com.gautama.bankhitscredit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TariffResponse {
    private UUID id;
    private String name;
    private BigDecimal annualRate;
    private int termDays;
    private LocalDateTime createdAt;
}