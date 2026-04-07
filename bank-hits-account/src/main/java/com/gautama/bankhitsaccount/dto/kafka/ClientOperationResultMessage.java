package com.gautama.bankhitsaccount.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientOperationResultMessage {
    private UUID operationId;
    private String status;
    private String errorCode;
    private String errorMessage;
    private UUID userId;
    private UUID accountId;
    private BigDecimal amount;
    private String currency;
    private String type;
    private UUID relatedAccountId;
    private String relatedAccountOwner;
    private LocalDateTime createdAt;
    private BigDecimal newBalance;
}
