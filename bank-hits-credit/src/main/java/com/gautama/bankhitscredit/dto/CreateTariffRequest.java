package com.gautama.bankhitscredit.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTariffRequest {
    @NotBlank(message = "Название тарифа обязательно")
    private String name;

    @NotNull
    @DecimalMin(value = "0.01", message = "Ставка должна быть больше 0")
    @DecimalMax(value = "999.99", message = "Ставка слишком большая")
    private BigDecimal annualRate;

    @NotNull
    @Positive
    private int termDays;
}