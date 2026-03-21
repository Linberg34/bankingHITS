package com.iisovaii.employee_bff.dto.response;

import com.iisovaii.employee_bff.dto.credit.CreditPaymentDto;
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
    private UUID paymentId;
    private BigDecimal amount;
    private LocalDateTime dueAt;
    private LocalDateTime paidAt;
    private CreditPaymentDto.PaymentStatus status;
}
