package com.gautama.bankhitsaccount.controller;

import com.gautama.bankhitsaccount.dto.AccountDTO;
import com.gautama.bankhitsaccount.dto.AccountListRequest;
import com.gautama.bankhitsaccount.dto.PageDTO;
import com.gautama.bankhitsaccount.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByUser(@PathVariable UUID userId) {
        log.info("Internal API - Getting accounts for user: {}", userId);
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
//        log.info("Internal API - Getting account by id: {}", id);
//        return ResponseEntity.ok(accountService.getAccountById(id));
//    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountDTO> getAccountByNumber(@PathVariable String accountNumber) {
        log.info("Internal API - Getting account by number: {}", accountNumber);
        return ResponseEntity.ok(accountService.getAccountByNumber(accountNumber));
    }

    @PostMapping()
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {
        log.info("Internal API - Creating account");
        return ResponseEntity.ok(accountService.createAccount(accountDTO));
    }

    @PostMapping("/current")
    public ResponseEntity<AccountDTO> createAccountCurrent(
            @RequestBody UUID userId,
            @RequestParam(defaultValue = "RUB") String currency) {
        log.info("Internal API - Creating account");
        return ResponseEntity.ok(accountService.createAccountCurrent(userId, currency));
    }

//    @PutMapping("/{id}/balance")
//    public ResponseEntity<Void> updateBalance(
//            @PathVariable Long id,
//            @RequestParam BigDecimal amount) {
//        log.info("Internal API - Updating balance for account: {}, amount: {}", id, amount);
//        accountService.updateBalance(id, amount);
//        return ResponseEntity.ok().build();
//    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable String accountNumber) {
        log.info("Internal API - Del account: {}, amount: {}", accountNumber);
        accountService.deleteAccount(accountNumber);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<PageDTO<AccountDTO>> getAllAccounts(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("Internal API - Getting all accounts with filters");

        AccountListRequest request = AccountListRequest.builder()
                .userId(userId)
                .status(status)
                .minBalance(minBalance)
                .maxBalance(maxBalance)
                .pageable(pageable)
                .build();

        Page<AccountDTO> page = accountService.getAllAccounts(request);
        return ResponseEntity.ok(PageDTO.fromPage(page));
    }
}
