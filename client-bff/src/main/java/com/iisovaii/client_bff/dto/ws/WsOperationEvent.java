package com.iisovaii.client_bff.dto.ws;

public record WsOperationEvent(
        WsEventType type,
        WsOperationDto operation
) {}
