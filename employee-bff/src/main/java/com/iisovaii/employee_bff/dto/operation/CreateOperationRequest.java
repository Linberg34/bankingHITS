package com.iisovaii.employee_bff.dto.operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOperationRequest {
    private String accountNumber;
    private String operationType;  // "DEPOSIT" | "WITHDRAWAL"
    private BigDecimal amount;
    private String description;
}