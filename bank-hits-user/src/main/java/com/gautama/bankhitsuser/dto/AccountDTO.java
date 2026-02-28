package com.gautama.bankhitsuser.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountDTO {
    private Long clientId;
    private Long accountId;
    private Double balance;
    private String status;
}
