package com.gautama.bankhitscredit.dto;

import com.gautama.bankhitscredit.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditPaymentResponse {
    private UUID id;
    private UUID creditId;
    private BigDecimal amount;
    private LocalDateTime dueAt;
    private LocalDateTime paidAt;
    private PaymentStatus status;
}
