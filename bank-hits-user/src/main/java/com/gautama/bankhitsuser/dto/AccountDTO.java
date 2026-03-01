package com.gautama.bankhitsuser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AccountDTO {
    private Long clientId;
    private String accountNumber;
    private BigDecimal balance;
    private String status;
}
