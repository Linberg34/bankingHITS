package com.iisovaii.employee_bff.client;

import com.iisovaii.employee_bff.config.FeignConfig;
import com.iisovaii.employee_bff.dto.response.AccountResponse;
import com.iisovaii.employee_bff.dto.response.AccountWithOwnerResponse;
import com.iisovaii.employee_bff.dto.response.OperationResponse;
import com.iisovaii.employee_bff.dto.response.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "account-service",
        url = "${services.account-service-url}",
        configuration = FeignConfig.class
)
public interface AccountServiceClient {

    @GetMapping("/accounts")
    List<AccountResponse> getAccountsByUserId(@RequestParam UUID userId);

    @GetMapping("/accounts/all")
    PageResponse<AccountWithOwnerResponse> getAllAccounts(
            @RequestParam int page,
            @RequestParam int size
    );

    @GetMapping("/accounts/{accountId}/operations")
    PageResponse<OperationResponse> getOperations(
            @PathVariable UUID accountId,
            @RequestParam int page,
            @RequestParam int size
    );
}
