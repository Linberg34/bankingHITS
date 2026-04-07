package com.gautama.bankhitsaccount.kafka;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OperationCommandMessage {
    private UUID operationId;
    private String type; // DEPOSIT, WITHDRAW, TRANSFER
    private UUID userId;
    private Map<String, Object> payload;
}
