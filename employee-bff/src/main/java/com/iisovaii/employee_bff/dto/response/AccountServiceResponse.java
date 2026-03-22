package com.iisovaii.employee_bff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountServiceResponse {
    private UUID clientId;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private String status;
}
