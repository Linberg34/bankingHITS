package com.iisovaii.client_bff.dto.credit;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreditPaymentDto(
        @JsonProperty("id") UUID paymentId,
        BigDecimal amount,
        LocalDateTime dueAt,
        LocalDateTime paidAt,
        PaymentStatus status
) {}

