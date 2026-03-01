package com.gautama.bankhitsuser.client;

import com.gautama.bankhitsuser.dto.AccountDTO;
import com.gautama.bankhitsuser.dto.CreateOperationRequest;
import com.gautama.bankhitsuser.dto.OperationDTO;
import com.gautama.bankhitsuser.dto.OperationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.cloud.openfeign.FeignClient;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "core", url = "${clients.core.url}")
public interface CoreClient {
    @GetMapping("/internal/accounts/my")
    List<AccountDTO> myAccounts();

    @GetMapping("/internal/accounts/by-user/{userId}")
    List<AccountDTO> accountsByUser(@PathVariable Long userId);

    @GetMapping("/internal/accounts/number/{accountNumber}")
    AccountDTO getAccountByNumber(@PathVariable("accountNumber") String accountNumber);

    @PostMapping("/internal/accounts")
    AccountDTO createAccount(@RequestBody AccountDTO accountDTO);

    @PutMapping("/internal/accounts/{id}")
    AccountDTO updateAccount(@PathVariable("id") Long id, @RequestBody AccountDTO accountDTO);

    @PatchMapping("/internal/accounts/{id}/balance")
    ResponseEntity<Void> updateBalance(@PathVariable("id") Long id, @RequestParam("amount") BigDecimal amount);

    @DeleteMapping("/internal/accounts/{id}")
    ResponseEntity<Void> deleteAccount(@PathVariable("id") Long id);

    @PutMapping("/internal/accounts/{id}/block")
    AccountDTO blockAccount(@PathVariable("id") Long id);

    @PutMapping("/internal/accounts/{id}/activate")
    AccountDTO activateAccount(@PathVariable("id") Long id);

    @GetMapping("/internal/accounts/{id}/balance")
    BigDecimal getAccountBalance(@PathVariable("id") Long id);

    @GetMapping("/internal/accounts/user/{userId}/active")
    List<AccountDTO> getActiveAccountsByUser(@PathVariable("userId") Long userId);

    // ============= Operation endpoints =============

    @PostMapping("/internal/operations/deposit")
    OperationResponse deposit(@RequestBody CreateOperationRequest request);

    @PostMapping("/internal/operations/withdraw")
    OperationResponse withdraw(@RequestBody CreateOperationRequest request);
//
//    @PostMapping("/internal/operations/transfer")
//    OperationResponse transfer(@RequestBody TransferRequest request);

    @GetMapping("/internal/operations/account/{accountId}")
    List<OperationDTO> getAccountOperations(
            @PathVariable("accountId") Long accountId,
            @RequestParam("page") int page,
            @RequestParam("size") int size);

    @GetMapping("/internal/operations/account/{accountId}/range")
    List<OperationDTO> getAccountOperationsByDateRange(
            @PathVariable("accountId") Long accountId,
            @RequestParam("start") String start,
            @RequestParam("end") String end);

    @GetMapping("/internal/operations/{id}")
    OperationDTO getOperationById(@PathVariable("id") Long id);

    @GetMapping("/internal/operations/user/{userId}")
    List<OperationDTO> getUserOperations(
            @PathVariable("userId") Long userId,
            @RequestParam("page") int page,
            @RequestParam("size") int size);
}