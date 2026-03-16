package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.account.AccountListResponse;
import com.iisovaii.client_bff.dto.account.CloseAccountResponse;
import com.iisovaii.client_bff.dto.account.OpenAccountRequest;
import com.iisovaii.client_bff.dto.account.OpenAccountResponse;
import com.iisovaii.client_bff.security.CurrentUser;
import com.iisovaii.client_bff.service.ProxyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bff/client/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final ProxyService proxyService;

    @GetMapping
    public ResponseEntity<AccountListResponse> getAccounts(
            @CurrentUser UUID userId) {
        return ResponseEntity.ok(proxyService.getAccounts(userId));
    }

    @PostMapping
    public ResponseEntity<OpenAccountResponse> openAccount(
            @CurrentUser UUID userId,
            @RequestBody @Valid OpenAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(proxyService.openAccount(userId, request));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<CloseAccountResponse> closeAccount(
            @CurrentUser UUID userId,
            @PathVariable UUID accountId) {
        return ResponseEntity.ok(proxyService.closeAccount(userId, accountId));
    }
}
