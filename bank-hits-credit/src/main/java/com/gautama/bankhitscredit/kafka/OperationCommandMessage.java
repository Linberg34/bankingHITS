package com.gautama.bankhitscredit.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationCommandMessage {
    private UUID operationId;
    private String type; // DEPOSIT, WITHDRAW
    private UUID userId;
    private Map<String, Object> payload;
}
