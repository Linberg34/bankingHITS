package com.iisovaii.employee_bff.dto.ws;

import com.iisovaii.employee_bff.dto.operation.OperationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsOperationEvent {
    private WsEventType type;
    private OperationDto operation;

    public enum WsEventType {
        OPERATION_ADDED, OPERATION_UPDATED, BALANCE_UPDATED
    }
}