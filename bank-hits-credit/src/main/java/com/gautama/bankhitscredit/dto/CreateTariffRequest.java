package com.gautama.bankhitscredit.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateTariffRequest {
    @NotBlank(message = "Название тарифа обязательно")
    private String name;

    @NotNull
    @DecimalMin(value = "0.01", message = "Ставка должна быть больше 0")
    @DecimalMax(value = "999.99", message = "Ставка слишком большая")
    private BigDecimal annualRate;
}