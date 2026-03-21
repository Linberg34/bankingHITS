package com.gautama.bankhitsuser.controller;

import com.gautama.bankhitsuser.client.AccountServiceClient;
import com.gautama.bankhitsuser.config.JwtUtil;
import com.gautama.bankhitsuser.dto.*;
import com.gautama.bankhitsuser.model.User;
import com.gautama.bankhitsuser.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountServiceClient accountServiceClient;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/my")
    public ResponseEntity<List<AccountDTO>> getMyAccounts(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Неверный токен.");
        }
        String token = authHeader.substring(7);
        User user = userService.loadUserByUsername(jwtUtil.extractUsername(token));

        List<AccountDTO> accounts = accountServiceClient.getAccountsByUser(user.getId());
        return ResponseEntity.ok(accounts);
    }

    /**
     * Получить счет по ID с проверкой принадлежности пользователю
     */
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountDTO> getAccountById(
            @PathVariable String accountNumber) {

        AccountDTO account = accountServiceClient.getAccountByNumber(accountNumber);

        return ResponseEntity.ok(account);
    }

    /**
     * Получить счет по юзеру
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByUser(
            @PathVariable Long userId) {

        List<AccountDTO> accounts = accountServiceClient.getAccountsByUser(userId);


        return ResponseEntity.ok(accounts);
    }

    /**
     * Создать новый счет для текущего пользователя
     */
    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(
            @RequestBody AccountDTO request) {

        AccountDTO accountDTO = AccountDTO.builder()
                .clientId(request.getClientId())
                .accountNumber(request.getAccountNumber())
                .balance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO)
                .currency(request.getCurrency() != null ? request.getCurrency() : "RUB")
                .status("ACTIVE")
                .build();

        AccountDTO createdAccount = accountServiceClient.createAccount(accountDTO);
        return ResponseEntity.ok(createdAccount);
    }

    /**
     * Создать новый счет для текущего пользователя
     */
    @PostMapping("/current")
    public ResponseEntity<AccountDTO> createAccountCurrent(
            HttpServletRequest request,
            @RequestParam(defaultValue = "RUB") String currency) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Неверный токен.");
        }
        String token = authHeader.substring(7);
        User user = userService.loadUserByUsername(jwtUtil.extractUsername(token));

        AccountDTO createdAccount = accountServiceClient.createAccountCurrent(user.getId(), currency);
        return ResponseEntity.ok(createdAccount);
    }

    @GetMapping("/list")
    public ResponseEntity<PageDTO<AccountDTO>> getAllAccounts(
            @RequestParam(required = false) Long filterUserId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        PageDTO<AccountDTO> accounts = accountServiceClient.getAllAccounts(
                filterUserId, status,
                minBalance, maxBalance, page, size, sort);

        return ResponseEntity.ok(accounts);
    }
//
//    /**
//     * Обновить счет
//     */
//    @PutMapping("/{accountId}")
//    public ResponseEntity<AccountDTO> updateAccount(
//            @PathVariable Long accountId,
//            @RequestBody AccountDTO accountDTO,
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to update account: {}", accountId);
//
//        // Проверяем существование и принадлежность счета
//        AccountDTO existingAccount = coreClient.getAccountById(accountId);
//        if (!existingAccount.getUserId().equals(userId)) {
//            return ResponseEntity.status(403).build();
//        }
//
//        accountDTO.setId(accountId);
//        accountDTO.setUserId(userId); // Не даем сменить владельца
//
//        AccountDTO updatedAccount = coreClient.updateAccount(accountId, accountDTO);
//        return ResponseEntity.ok(updatedAccount);
//    }
//
//    /**
//     * Заблокировать счет
//     */
//    @PutMapping("/{accountId}/block")
//    public ResponseEntity<AccountDTO> blockAccount(
//            @PathVariable Long accountId,
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to block account: {}", accountId);
//
//        // Проверяем принадлежность счета
//        AccountDTO existingAccount = coreClient.getAccountById(accountId);
//        if (!existingAccount.getUserId().equals(userId)) {
//            return ResponseEntity.status(403).build();
//        }
//
//        AccountDTO blockedAccount = coreClient.blockAccount(accountId);
//        return ResponseEntity.ok(blockedAccount);
//    }
//
//    /**
//     * Активировать счет
//     */
//    @PutMapping("/{accountId}/activate")
//    public ResponseEntity<AccountDTO> activateAccount(
//            @PathVariable Long accountId,
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to activate account: {}", accountId);
//
//        // Проверяем принадлежность счета
//        AccountDTO existingAccount = coreClient.getAccountById(accountId);
//        if (!existingAccount.getUserId().equals(userId)) {
//            return ResponseEntity.status(403).build();
//        }
//
//        AccountDTO activatedAccount = coreClient.activateAccount(accountId);
//        return ResponseEntity.ok(activatedAccount);
//    }
//
    /**
     * Закрыть счет (мягкое удаление)
     */
    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> closeAccount(
            @PathVariable String accountNumber) {

        accountServiceClient.deleteAccount(accountNumber);
        return ResponseEntity.noContent().build();
    }
//
//    /**
//     * Получить баланс счета
//     */
//    @GetMapping("/{accountId}/balance")
//    public ResponseEntity<BigDecimal> getAccountBalance(
//            @PathVariable Long accountId) {
//
//        BigDecimal balance = coreClient.getAccountBalance(accountId);
//        return ResponseEntity.ok(balance);
//    }
//
//    /**
//     * Получить все активные счета пользователя
//     */
//    @GetMapping("/my/active")
//    public ResponseEntity<List<AccountDTO>> getMyActiveAccounts(
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to get active accounts for user: {}", userId);
//        List<AccountDTO> accounts = coreClient.getActiveAccountsByUser(userId);
//        return ResponseEntity.ok(accounts);
//    }
//
    // ============= Операции со счетами =============

    /**
     * Внести деньги на счет
     */
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<OperationResponse> deposit(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description) {

        CreateOperationRequest request = CreateOperationRequest.builder()
                .accountNumber(accountNumber)
                .amount(amount)
                .description(description != null ? description : "Deposit via user-service")
                .operationType("DEPOSIT")
                .build();

        OperationResponse response = accountServiceClient.deposit(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Снять деньги со счета
     */
    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<OperationResponse> withdraw(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description) {

        // Проверяем принадлежность счета
        AccountDTO account = accountServiceClient.getAccountByNumber(accountNumber);

        // Проверяем достаточно ли средств
        if (account.getBalance().compareTo(amount) < 0) {
            return ResponseEntity.badRequest().body(
                    OperationResponse.builder()
                            .message("Insufficient funds")
                            .build()
            );
        }

        CreateOperationRequest request = CreateOperationRequest.builder()
                .accountNumber(accountNumber)
                .amount(amount)
                .description(description != null ? description : "Withdrawal via user-service")
                .operationType("WITHDRAWAL")
                .build();

        OperationResponse response = accountServiceClient.withdraw(request);
        return ResponseEntity.ok(response);
    }
//
//    /**
//     * Перевести деньги между счетами
//     */
    @PostMapping("/transfer")
    public ResponseEntity<OperationResponse> transfer(
            HttpServletRequest request,
            @RequestBody TransferRequest transferRequest) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Неверный токен.");
        }
        String token = authHeader.substring(7);
        User user = userService.loadUserByUsername(jwtUtil.extractUsername(token));

        AccountDTO fromAccount = accountServiceClient.getAccountByNumber(transferRequest.getFromAccountNumber());
        AccountDTO toAccount = accountServiceClient.getAccountByNumber(transferRequest.getToAccountNumber());

        if (!user.getId().equals(fromAccount.getClientId()) || !user.getId().equals(toAccount.getClientId())) {
            throw new BadCredentialsException("Можно переводить только между своими счетами.");
        }

        OperationResponse response = accountServiceClient.transfer(transferRequest);
        return ResponseEntity.ok(response);
    }
//
//    // ============= Операции (история) =============
//
    /**
     * Получить операции по счету
     */
    @GetMapping("/{accountNumber}/operations/page")
    public ResponseEntity<List<OperationDTO>> getAccountOperationsPage(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Проверяем принадлежность счета
        AccountDTO account = accountServiceClient.getAccountByNumber(accountNumber);

        List<OperationDTO> operations = accountServiceClient.getAccountOperationsPage(accountNumber, page, size);
        return ResponseEntity.ok(operations);
    }

    /**
     * Получить операции по счету
     */
    @GetMapping("/{accountNumber}/operations")
    public ResponseEntity<List<OperationDTO>> getAccountOperations(
            @PathVariable String accountNumber) {

        List<OperationDTO> operations = accountServiceClient.getAccountOperations(accountNumber);
        return ResponseEntity.ok(operations);
    }

//    /**
//     * Получить операции по счету за период
//     */
//    @GetMapping("/{accountId}/operations/range")
//    public ResponseEntity<List<OperationDTO>> getAccountOperationsByDateRange(
//            @PathVariable Long accountId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to get operations for account {} between {} and {}", accountId, start, end);
//
//        // Проверяем принадлежность счета
//        AccountDTO account = coreClient.getAccountById(accountId);
//        if (!account.getUserId().equals(userId)) {
//            return ResponseEntity.status(403).build();
//        }
//
//        List<OperationDTO> operations = coreClient.getAccountOperationsByDateRange(
//                accountId, start.toString(), end.toString());
//        return ResponseEntity.ok(operations);
//    }
//
//    /**
//     * Получить сводку по операциям счета
//     */
//    @GetMapping("/{accountId}/operations/summary")
//    public ResponseEntity<AccountOperationsSummaryDTO> getAccountOperationsSummary(
//            @PathVariable Long accountId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to get operations summary for account: {}", accountId);
//
//        // Проверяем принадлежность счета
//        AccountDTO account = coreClient.getAccountById(accountId);
//        if (!account.getUserId().equals(userId)) {
//            return ResponseEntity.status(403).build();
//        }
//
//        AccountOperationsSummaryDTO summary = coreClient.getAccountOperationsSummary(
//                accountId, start.toString(), end.toString());
//        return ResponseEntity.ok(summary);
//    }
//
//    /**
//     * Получить последнюю операцию по счету
//     */
//    @GetMapping("/{accountId}/operations/last")
//    public ResponseEntity<OperationDTO> getLastOperation(
//            @PathVariable Long accountId,
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to get last operation for account: {}", accountId);
//
//        // Проверяем принадлежность счета
//        AccountDTO account = coreClient.getAccountById(accountId);
//        if (!account.getUserId().equals(userId)) {
//            return ResponseEntity.status(403).build();
//        }
//
//        OperationDTO operation = coreClient.getLastOperation(accountId);
//        return operation != null ? ResponseEntity.ok(operation) : ResponseEntity.noContent().build();
//    }
//
//    /**
//     * Получить оборот по счету за период
//     */
//    @GetMapping("/{accountId}/turnover")
//    public ResponseEntity<BigDecimal> getAccountTurnover(
//            @PathVariable Long accountId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to get turnover for account {} between {} and {}", accountId, start, end);
//
//        // Проверяем принадлежность счета
//        AccountDTO account = coreClient.getAccountById(accountId);
//        if (!account.getUserId().equals(userId)) {
//            return ResponseEntity.status(403).build();
//        }
//
//        BigDecimal turnover = coreClient.getAccountTurnover(accountId, start.toString(), end.toString());
//        return ResponseEntity.ok(turnover);
//    }
//
//    /**
//     * Получить детали операции
//     */
//    @GetMapping("/operations/{operationId}")
//    public ResponseEntity<OperationDTO> getOperationById(
//            @PathVariable Long operationId) {
//
//        OperationDTO operation = coreClient.getOperationById(operationId);
//
//        return ResponseEntity.ok(operation);
//    }
//
////    /**
////     * Получить операции по ID транзакции
////     */
////    @GetMapping("/operations/transaction/{transactionId}")
////    public ResponseEntity<List<OperationDTO>> getOperationsByTransactionId(
////            @PathVariable String transactionId,) {
////        log.info("REST request to get operations by transactionId: {}", transactionId);
////
////        List<OperationDTO> operations = coreClient.getOperationsByTransactionId(transactionId);
////
////        // Проверяем, что все операции принадлежат пользователю
////        for (OperationDTO operation : operations) {
////            AccountDTO account = coreClient.getAccountById(operation.getAccountId());
////            if (!account.getUserId().equals(userId)) {
////                return ResponseEntity.status(403).build();
////            }
////        }
////
////        return ResponseEntity.ok(operations);
////    }
//
//    /**
//     * Получить все операции пользователя
//     */
//    @GetMapping("/my/operations")
//    public ResponseEntity<List<OperationDTO>> getMyOperations(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to get operations for user: {}", userId);
//
//        List<OperationDTO> operations = coreClient.getUserOperations(userId, page, size);
//        return ResponseEntity.ok(operations);
//    }
}
