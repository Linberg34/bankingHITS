package com.iisovaii.employee_bff.dto.operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationAcceptedResponse {
    private UUID operationId;
    private OperationDto.OperationStatus status;
}