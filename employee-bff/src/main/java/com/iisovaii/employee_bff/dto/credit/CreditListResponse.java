package com.iisovaii.employee_bff.dto.credit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditListResponse {
    private List<CreditSummaryDto> credits;
}