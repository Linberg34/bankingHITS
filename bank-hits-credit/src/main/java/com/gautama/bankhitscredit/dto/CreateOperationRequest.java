package com.gautama.bankhitscredit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOperationRequest {
    private String accountNumber;
    private String operationType;
    private BigDecimal amount;
    private String description;
}
