package com.iisovaii.employee_bff.dto.credit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDetailEmployeeResponse {
    private UUID creditId;
    private UUID userId;
    private String ownerFullName;
    private UUID accountId;
    private BigDecimal amount;
    private BigDecimal remainingDebt;
    private BigDecimal interestRate;
    private String tariffName;
    private CreditSummaryDto.CreditStatus status;
    private LocalDateTime issuedAt;
    private LocalDateTime nextPaymentAt;
    private List<CreditPaymentDto> payments;
}
