package com.iisovaii.client_bff.dto.credit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TakeCreditResponse(
        UUID creditId,
        BigDecimal amount,
        BigDecimal remainingDebt,
        BigDecimal interestRate,
        String tariffName,
        CreditStatus status,
        LocalDateTime nextPaymentAt
) {}

