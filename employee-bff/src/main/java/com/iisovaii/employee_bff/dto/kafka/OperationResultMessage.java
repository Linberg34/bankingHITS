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
public class OperationResultMessage {
    private UUID correlationId;
    private OperationDto.OperationStatus status;
    private String failReason;        // nullable
    private UUID operationId;
    private BigDecimal newBalance;
    private Instant processedAt;
}
