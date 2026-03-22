package com.iisovaii.employee_bff.dto.ws;

import com.iisovaii.employee_bff.dto.operation.OperationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsBalanceEvent {
    private WsOperationEvent.WsEventType type;
    private UUID accountId;
    private BigDecimal newBalance;
    private OperationDto.Currency currency;
}
