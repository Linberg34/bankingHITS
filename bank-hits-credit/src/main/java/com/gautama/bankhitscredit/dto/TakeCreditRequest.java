package com.gautama.bankhitscredit.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TakeCreditRequest {
    @NotNull
    private Long clientId;

    @NotNull
    private Long accountId;   // счёт в Ядре для зачисления и списания

    @NotNull
    private Long tariffId;

    @NotNull
    @DecimalMin(value = "1.00", message = "Сумма кредита должна быть больше 0")
    private BigDecimal amount;
}