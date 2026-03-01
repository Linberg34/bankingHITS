package com.gautama.bankhitsuser.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TakeCreditRequest {
    @NotNull
    private String accountNumber;

    @NotNull
    private Long tariffId;

    @NotNull
    @DecimalMin(value = "1.00", message = "Сумма кредита должна быть больше 0")
    private BigDecimal amount;
}