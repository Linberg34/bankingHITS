package com.iisovaii.employee_bff.controller;

import com.iisovaii.employee_bff.dto.account.AccountListResponse;
import com.iisovaii.employee_bff.dto.account.AllAccountsPageResponse;
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
public class AccountController {

    private final ProxyService proxyService;

    // все счета всех клиентов
    @GetMapping("/accounts")
    public ResponseEntity<AllAccountsPageResponse> getAllAccounts(
            @CurrentUser UUID employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                proxyService.getAllAccounts(page, size)
        );
    }

    // счета конкретного клиента
    @GetMapping("/clients/{clientId}/accounts")
    public ResponseEntity<AccountListResponse> getClientAccounts(
            @CurrentUser UUID employeeId,
            @PathVariable UUID clientId) {
        return ResponseEntity.ok(
                proxyService.getClientAccounts(clientId)
        );
    }
}