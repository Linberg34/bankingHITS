package com.iisovaii.client_bff.dto.credit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreditPaymentResponse(
        UUID id,
        UUID creditId,
        BigDecimal amount,
        LocalDateTime dueAt,
        LocalDateTime paidAt,
        String status
) {}