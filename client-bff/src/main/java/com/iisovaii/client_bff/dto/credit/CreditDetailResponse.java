package com.iisovaii.client_bff.dto.credit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreditDetailResponse(
        UUID creditId,
        UUID clientId,
        String accountNumber,
        BigDecimal amount,
        BigDecimal remainingDebt,
        BigDecimal interestRate,
        String tariffName,
        String status,
        LocalDateTime issuedAt,
        LocalDateTime nextPaymentAt,
        List<CreditPaymentDto> payments
) {}