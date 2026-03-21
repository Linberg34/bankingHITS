package com.iisovaii.employee_bff.controller;

import com.iisovaii.employee_bff.dto.operation.OperationPageResponse;
import com.iisovaii.employee_bff.security.CurrentUser;
import com.iisovaii.employee_bff.service.ProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/bff/employee")
@RequiredArgsConstructor
public class OperationController {

    private final ProxyService proxyService;

    // история операций по любому счёту
    @GetMapping("/accounts/{accountId}/operations")
    public ResponseEntity<OperationPageResponse> getOperations(
            @CurrentUser UUID employeeId,
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                proxyService.getOperations(accountId, page, size)
        );
    }
}