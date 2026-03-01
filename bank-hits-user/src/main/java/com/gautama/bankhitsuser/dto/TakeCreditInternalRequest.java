package com.gautama.bankhitsuser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TakeCreditInternalRequest {
    private Long clientId;
    private Long accountId;
    private Long tariffId;
    private BigDecimal amount;
}