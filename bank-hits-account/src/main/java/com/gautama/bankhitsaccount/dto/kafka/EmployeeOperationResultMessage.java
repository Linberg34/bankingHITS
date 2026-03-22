package com.gautama.bankhitsaccount.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeOperationResultMessage {
    private UUID correlationId;
    private String status;
    private String failReason;
    private UUID operationId;
    private BigDecimal newBalance;
    private Instant processedAt;
}
