package com.gautama.bankhitsuser.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreditResponse {
    private Long id;
    private Long clientId;
    private Long accountId;
    private String tariffName;
    private BigDecimal annualRate;
    private BigDecimal principalAmount;
    private BigDecimal remainingDebt;
    private LocalDateTime issuedAt;
    private LocalDateTime closedAt;
    private String  status;
}