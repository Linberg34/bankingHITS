package com.iisovaii.employee_bff.dto.operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationPageResponse {
    private List<OperationDto> content;
    private int page;
    private int size;
    private long totalElements;
}
