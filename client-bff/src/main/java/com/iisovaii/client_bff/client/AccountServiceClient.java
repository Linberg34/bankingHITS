package com.iisovaii.client_bff.client;

import com.iisovaii.client_bff.config.FeignConfig;
import com.iisovaii.client_bff.dto.account.AccountListResponse;
import com.iisovaii.client_bff.dto.account.CloseAccountResponse;
import com.iisovaii.client_bff.dto.account.AccountDto;
import com.iisovaii.client_bff.dto.account.OpenAccountRequest;
import com.iisovaii.client_bff.dto.account.OpenAccountResponse;
import com.iisovaii.client_bff.dto.operation.OperationPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "account-service",
        url = "${services.account-service-url}",
        configuration = FeignConfig.class
)
public interface AccountServiceClient {
    @GetMapping("/internal/accounts/by-user/{userId}")
    List<AccountDto> getAccounts(@PathVariable("userId") UUID userId);

    @GetMapping("/internal/accounts/number/{accountNumber}")
    AccountDto getAccountByNumber(@PathVariable("accountNumber") String accountNumber);

    @PostMapping("/internal/accounts/current")
    OpenAccountResponse openAccount(
            @RequestBody UUID userId,
            @RequestParam("currency") String currency
    );

    @DeleteMapping("/internal/accounts/{accountNumber}")
    void closeAccount(@PathVariable("accountNumber") String accountNumber);

    @GetMapping("/internal/operations/account/{accountNumber}/page")
    List<com.iisovaii.client_bff.dto.operation.OperationDto> getOperations(
            @PathVariable("accountNumber") String accountNumber,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );
}

