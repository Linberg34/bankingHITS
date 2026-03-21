package com.gautama.bankhitscredit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationResponse {
    private OperationDTO operation;
    private AccountDTO account;
    private String message;
}