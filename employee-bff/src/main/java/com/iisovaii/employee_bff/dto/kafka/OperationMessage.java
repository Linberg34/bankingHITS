package com.iisovaii.employee_bff.dto.kafka;

import com.iisovaii.employee_bff.dto.operation.OperationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationMessage {
    private UUID operationId;
    private OperationDto.OperationType type;
    private String accountId;
    private BigDecimal amount;
    private OperationDto.Currency currency;
    private String targetAccountId;     // nullable
    private UUID initiatedByUserId;
    private Instant sentAt;
}