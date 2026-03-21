package com.iisovaii.client_bff.client;

import com.iisovaii.client_bff.config.FeignConfig;
import com.iisovaii.client_bff.dto.account.AccountListResponse;
import com.iisovaii.client_bff.dto.account.CloseAccountResponse;
import com.iisovaii.client_bff.dto.account.OpenAccountRequest;
import com.iisovaii.client_bff.dto.account.OpenAccountResponse;
import com.iisovaii.client_bff.dto.operation.OperationPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "account-service",
        url = "${services.account-service-url}",
        configuration = FeignConfig.class
)
public interface AccountServiceClient {
    @GetMapping("/internal/accounts")
    AccountListResponse getAccounts(@RequestParam("userId") UUID userId);

    @PostMapping("/internal/accounts")
    OpenAccountResponse openAccount(
            @RequestParam("userId") UUID userId,
            @RequestBody OpenAccountRequest request
    );

    @PostMapping("/internal/accounts/{accountId}/close")
    CloseAccountResponse closeAccount(
            @PathVariable("accountId") UUID accountId,
            @RequestParam("userId") UUID userId
    );

    @GetMapping("/internal/accounts/{accountId}/operations")
    OperationPageResponse getOperations(
            @PathVariable("accountId") UUID accountId,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );

    @GetMapping("/internal/accounts/{accountId}/ownership")
    void checkAccountOwnership(
            @PathVariable("accountId") UUID accountId,
            @RequestParam("userId") UUID userId
    );
}

