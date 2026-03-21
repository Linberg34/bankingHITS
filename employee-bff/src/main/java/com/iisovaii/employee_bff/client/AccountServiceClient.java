package com.iisovaii.employee_bff.client;

import com.iisovaii.employee_bff.config.FeignConfig;
import com.iisovaii.employee_bff.dto.operation.CreateOperationRequest;
import com.iisovaii.employee_bff.dto.response.AccountResponse;
import com.iisovaii.employee_bff.dto.response.AccountWithOwnerResponse;
import com.iisovaii.employee_bff.dto.response.OperationResponse;
import com.iisovaii.employee_bff.dto.response.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// client/AccountServiceClient.java
@FeignClient(
        name = "account-service",
        url = "${services.account-service-url}",
        configuration = FeignConfig.class
)
public interface AccountServiceClient {

    @GetMapping("/internal/accounts/by-user/{userId}")
    List<AccountResponse> getAccountsByUserId(
            @PathVariable("userId") UUID userId  // ← явное имя
    );

    @GetMapping("/internal/accounts/number/{accountNumber}")
    AccountResponse getAccountByNumber(
            @PathVariable("accountNumber") String accountNumber
    );

    @GetMapping("/internal/accounts/list")
    PageResponse<AccountWithOwnerResponse> getAllAccounts(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );

    @GetMapping("/internal/operations/account/{accountNumber}/page")
    PageResponse<OperationResponse> getOperations(
            @PathVariable("accountNumber") String accountNumber,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );

    @PostMapping("/internal/operations/deposit")
    OperationResponse deposit(
            @RequestBody CreateOperationRequest request
    );

    @PostMapping("/internal/operations/withdraw")
    OperationResponse withdraw(
            @RequestBody CreateOperationRequest request
    );

    @DeleteMapping("/internal/accounts/{accountNumber}")
    void deleteAccount(
            @PathVariable("accountNumber") String accountNumber
    );
}
