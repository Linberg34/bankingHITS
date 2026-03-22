package com.iisovaii.employee_bff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationServiceResponse {
    private UUID id;
    private String accountNumber;
    private String currency;
    private String operationType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String status;
    private String description;
    private LocalDateTime createdAt;
}
