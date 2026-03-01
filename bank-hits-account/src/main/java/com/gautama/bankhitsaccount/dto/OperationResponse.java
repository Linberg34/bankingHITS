package com.gautama.bankhitsaccount.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationResponse {
    private OperationDTO operation;
    private AccountDTO account;
    private String message;
}
