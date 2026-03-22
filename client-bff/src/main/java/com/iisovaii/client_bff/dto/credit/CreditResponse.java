package com.iisovaii.client_bff.dto.credit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreditResponse(
        UUID id,
        UUID clientId,
        String accountNumber,
        String tariffName,
        BigDecimal annualRate,
        int termDays,
        BigDecimal principalAmount,
        BigDecimal remainingDebt,
        LocalDateTime issuedAt,
        LocalDateTime closedAt,
        LocalDateTime nextPaymentAt,
        String status
) {}
