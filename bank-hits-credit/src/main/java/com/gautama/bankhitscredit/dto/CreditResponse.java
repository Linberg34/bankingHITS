package com.gautama.bankhitscredit.dto;

import com.gautama.bankhitscredit.enums.CreditStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditResponse {
    private UUID id;
    private UUID clientId;
    private String accountNumber;
    private String tariffName;
    private BigDecimal annualRate;
    private int termDays;
    private BigDecimal principalAmount;
    private BigDecimal remainingDebt;
    private LocalDateTime issuedAt;
    private LocalDateTime closedAt;
    private LocalDateTime nextPaymentAt;
    private CreditStatus status;
}