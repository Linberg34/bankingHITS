package com.gautama.bankhitsuser.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TariffResponse {
    private Long id;
    private String name;
    private BigDecimal annualRate;
    private LocalDateTime createdAt;
}