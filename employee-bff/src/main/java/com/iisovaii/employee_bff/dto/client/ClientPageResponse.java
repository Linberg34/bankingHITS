package com.iisovaii.employee_bff.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientPageResponse {
    private List<ClientSummaryDto> content;
    private int page;
    private int size;
    private long totalElements;
}