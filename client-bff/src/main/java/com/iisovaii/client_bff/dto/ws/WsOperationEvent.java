package com.iisovaii.client_bff.dto.ws;

import com.iisovaii.client_bff.dto.operation.OperationDto;

public record WsOperationEvent(
        WsEventType type,
        OperationDto operation
) {}

