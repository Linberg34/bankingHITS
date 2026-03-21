package com.iisovaii.employee_bff.dto.response;

import com.iisovaii.employee_bff.dto.operation.OperationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationResponse {
    private UUID operationId;
    private OperationDto.OperationType type;
    private BigDecimal amount;
    private OperationDto.Currency currency;
    private UUID relatedAccountId;
    private String relatedAccountOwner;
    private OperationDto.OperationStatus status;
    private String failReason;
    private LocalDateTime createdAt;
}
