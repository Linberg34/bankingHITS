package com.iisovaii.employee_bff.client;

import com.iisovaii.employee_bff.config.FeignConfig;
import com.iisovaii.employee_bff.dto.operation.CreateOperationRequest;
import com.iisovaii.employee_bff.dto.operation.TransferRequest;
import com.iisovaii.employee_bff.dto.response.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "account-service",
        url = "${services.account-service-url}",
        configuration = FeignConfig.class
)
public interface AccountServiceClient {

    @GetMapping("/internal/accounts/by-user/{userId}")
    List<AccountServiceResponse> getAccountsByUserId(
            @PathVariable("userId") UUID userId
    );

    @GetMapping("/internal/accounts/number/{accountNumber}")
    AccountServiceResponse getAccountByNumber(
            @PathVariable("accountNumber") String accountNumber
    );

    @GetMapping("/internal/accounts/list")
    PageDTOAccountDTO getAllAccounts(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    );

    @GetMapping("/internal/operations/account/{accountNumber}/page")
    List<OperationServiceResponse> getOperations(
            @PathVariable("accountNumber") String accountNumber,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    );

    @PostMapping("/internal/operations/deposit")
    OperationResponse deposit(
            @RequestBody CreateOperationRequest request
    );

    @PostMapping("/internal/operations/withdraw")
    OperationResponse withdraw(
            @RequestBody CreateOperationRequest request
    );

    @PostMapping("/internal/operations/transfer")
    OperationResponse transfer(
            @RequestBody TransferRequest request
    );

    @DeleteMapping("/internal/accounts/{accountNumber}")
    void deleteAccount(
            @PathVariable("accountNumber") String accountNumber
    );
}
