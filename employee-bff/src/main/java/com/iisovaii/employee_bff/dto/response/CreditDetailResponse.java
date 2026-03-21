package com.iisovaii.employee_bff.dto.response;

import com.iisovaii.employee_bff.dto.credit.CreditSummaryDto;
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
public class CreditDetailResponse {
    private UUID creditId;
    private UUID userId;
    private String firstName;      // склеиваем в ownerFullName в маппере
    private String lastName;
    private UUID accountId;
    private BigDecimal amount;
    private BigDecimal remainingDebt;
    private BigDecimal interestRate;
    private String tariffName;
    private CreditSummaryDto.CreditStatus status;
    private LocalDateTime issuedAt;
    private LocalDateTime nextPaymentAt;
    private List<CreditPaymentResponse> payments;
}
