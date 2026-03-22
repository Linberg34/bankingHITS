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
public class CreditPaymentDto {
    private UUID paymentId;
    private BigDecimal amount;
    private LocalDateTime dueAt;
    private LocalDateTime paidAt;  // nullable
    private PaymentStatus status;

    public enum PaymentStatus {
        PENDING, PAID, OVERDUE
    }
}
