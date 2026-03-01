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

@FeignClient(name = "account", url = "${clients.account.url}")
public interface CoreClient {
    @GetMapping("/internal/accounts/by-user/{userId}")
    List<AccountDTO> getAccountsByUser(@PathVariable Long userId);

    @GetMapping("/internal/accounts/number/{accountNumber}")
    AccountDTO getAccountByNumber(@PathVariable("accountNumber") String accountNumber);

    @PostMapping("/internal/accounts")
    AccountDTO createAccount(@RequestBody AccountDTO accountDTO);

    @PostMapping("/internal/accounts/current")
    AccountDTO createAccountCurrent(@RequestBody Long userId);

    @PutMapping("/internal/internal/accounts/{id}")
    AccountDTO updateAccount(@PathVariable("id") Long id, @RequestBody AccountDTO accountDTO);

    @PatchMapping("/internal/accounts/{id}/balance")
    ResponseEntity<Void> updateBalance(@PathVariable("id") Long id, @RequestParam("amount") BigDecimal amount);

    @DeleteMapping("/internal/accounts/{id}")
    ResponseEntity<Void> deleteAccount(@PathVariable("id") Long id);
//
//    @PutMapping("/internal/accounts/{id}/block")
//    AccountDTO blockAccount(@PathVariable("id") Long id);
//
//    @PutMapping("/internal/accounts/{id}/activate")
//    AccountDTO activateAccount(@PathVariable("id") Long id);
//
//    @GetMapping("/internal/accounts/{id}/balance")
//    BigDecimal getAccountBalance(@PathVariable("id") Long id);
//
//    @GetMapping("/internal/accounts/user/{userId}/active")
//    List<AccountDTO> getActiveAccountsByUser(@PathVariable("userId") Long userId);

    // ============= Operation endpoints =============

    @PostMapping("/internal/operations/deposit")
    OperationResponse deposit(@RequestBody CreateOperationRequest request);

    @PostMapping("/internal/operations/withdraw")
    OperationResponse withdraw(@RequestBody CreateOperationRequest request);
//
//    @PostMapping("/internal/operations/transfer")
//    OperationResponse transfer(@RequestBody TransferRequest request);

    @GetMapping("/internal/operations/account/{accountNumber}/page")
    List<OperationDTO> getAccountOperationsPage(
            @PathVariable("accountId") String accountNumber,
            @RequestParam("page") int page,
            @RequestParam("size") int size);

    @GetMapping("/internal/operations/account/{accountNumber}")
    List<OperationDTO> getAccountOperations(
            @PathVariable("accountId") String accountNumber);

    @GetMapping("/internal/internal/operations/account/{accountNumber}/range")
    List<OperationDTO> getAccountOperationsByDateRange(
            @PathVariable("accountId") String accountNumber,
            @RequestParam("start") String start,
            @RequestParam("end") String end);

    @GetMapping("/internal/internal/operations/{id}")
    OperationDTO getOperationById(@PathVariable("id") Long id);

    @GetMapping("/internal/internal/operations/user/{userId}")
    List<OperationDTO> getUserOperations(
            @PathVariable("userId") Long userId,
            @RequestParam("page") int page,
            @RequestParam("size") int size);
}