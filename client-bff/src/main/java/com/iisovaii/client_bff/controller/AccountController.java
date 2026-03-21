package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.account.AccountListResponse;
import com.iisovaii.client_bff.dto.account.CloseAccountResponse;
import com.iisovaii.client_bff.dto.account.OpenAccountRequest;
import com.iisovaii.client_bff.dto.account.OpenAccountResponse;
import com.iisovaii.client_bff.security.CurrentUser;
import com.iisovaii.client_bff.service.ProxyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bff/client/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Работа со счетами клиента")
public class AccountController {

    private final ProxyService proxyService;

    @GetMapping
    @Operation(
            summary = "Список счетов клиента",
            description = "Возвращает все счета текущего клиента с балансами и статусами."
    )
    public ResponseEntity<AccountListResponse> getAccounts(
            @CurrentUser UUID userId) {
        return ResponseEntity.ok(proxyService.getAccounts(userId));
    }

    @PostMapping
    @Operation(
            summary = "Открыть новый счет",
            description = "Открывает новый счет через AccountService и возвращает созданный счет."
    )
    public ResponseEntity<OpenAccountResponse> openAccount(
            @CurrentUser UUID userId,
            @RequestBody @Valid OpenAccountRequest request) {
        OpenAccountResponse response = proxyService.openAccount(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{accountId}")
    @Operation(
            summary = "Закрыть счет",
            description = "Закрывает счет через AccountService и возвращает итоговый статус счета."
    )
    public ResponseEntity<CloseAccountResponse> closeAccount(
            @CurrentUser UUID userId,
            @PathVariable String accountId) {
        proxyService.checkAccountOwnership(userId, accountId);
        CloseAccountResponse response = proxyService.closeAccount(userId, accountId);
        return ResponseEntity.ok(response);
    }
}
