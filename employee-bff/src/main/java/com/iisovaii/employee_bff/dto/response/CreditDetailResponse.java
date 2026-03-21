package com.iisovaii.employee_bff.dto.response;

import com.iisovaii.employee_bff.dto.credit.CreditSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDetailResponse {
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
    private CreditSummaryDto.CreditStatus status;
}
