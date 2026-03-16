package com.iisovaii.employee_bff.dto.credit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditSummaryDto {
    private UUID creditId;
    private BigDecimal amount;
    private BigDecimal remainingDebt;
    private BigDecimal interestRate;
    private String tariffName;
    private CreditStatus status;
    private LocalDateTime nextPaymentAt;

    public enum CreditStatus {
        ACTIVE, CLOSED, OVERDUE
    }
}