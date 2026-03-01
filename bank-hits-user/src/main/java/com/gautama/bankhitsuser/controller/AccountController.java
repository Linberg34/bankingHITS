package com.gautama.bankhitsuser.controller;

import com.gautama.bankhitsuser.client.CoreClient;
import com.gautama.bankhitsuser.config.JwtUtil;
import com.gautama.bankhitsuser.dto.AccountDTO;
import com.gautama.bankhitsuser.dto.OperationDTO;
import com.gautama.bankhitsuser.dto.UserDTO;
import com.gautama.bankhitsuser.dto.UserFullDTO;
import com.gautama.bankhitsuser.enums.UserQueryType;
import com.gautama.bankhitsuser.mapper.UserMapper;
import com.gautama.bankhitsuser.model.User;
import com.gautama.bankhitsuser.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final CoreClient coreClient;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/api/accounts")
    public List<AccountDTO> myAccounts() {
        return coreClient.myAccounts();
    }

    @GetMapping("/my")
    public ResponseEntity<List<AccountDTO>> getMyAccounts(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Неверный токен.");
        }
        String token = authHeader.substring(7);
        User user = userService.loadUserByUsername(jwtUtil.extractUsername(token));

        List<AccountDTO> accounts = coreClient.getActiveAccountsByUser(user.getId());
        return ResponseEntity.ok(accounts);
    }

//    /**
//     * Получить счет по ID с проверкой принадлежности пользователю
//     */
//    @GetMapping("/{accountId}")
//    public ResponseEntity<AccountDTO> getAccountById(
//            @PathVariable Long accountId,
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to get account {} for user {}", accountId, userId);
//
//        AccountDTO account = coreClient.getAccountById(accountId);
//
//        // Проверяем, что счет принадлежит пользователю
//        if (!account.getUserId().equals(userId)) {
//            log.warn("User {} attempted to access account {} belonging to user {}",
//                    userId, accountId, account.getUserId());
//            return ResponseEntity.status(403).build();
//        }
//
//        return ResponseEntity.ok(account);
//    }
//
//    /**
//     * Получить счет по номеру
//     */
//    @GetMapping("/number/{accountNumber}")
//    public ResponseEntity<AccountDTO> getAccountByNumber(
//            @PathVariable String accountNumber,
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to get account by number: {}", accountNumber);
//
//        AccountDTO account = coreClient.getAccountByNumber(accountNumber);
//
//        // Проверяем, что счет принадлежит пользователю
//        if (!account.getUserId().equals(userId)) {
//            log.warn("User {} attempted to access account {} belonging to user {}",
//                    userId, accountNumber, account.getUserId());
//            return ResponseEntity.status(403).build();
//        }
//
//        return ResponseEntity.ok(account);
//    }
//
//    /**
//     * Создать новый счет для текущего пользователя
//     */
//    @PostMapping
//    public ResponseEntity<AccountDTO> createAccount(
//            @RequestBody AccountDTO request) {
//        log.info("REST request to create account for user: {}", request.getClientId());
//
//        AccountDTO accountDTO = AccountDTO.builder()
//                .clientId(request.getClientId())
//                .accountId(request.getAccountId())
//                .balance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO)
//                .status("ACTIVE")
//                .build();
//
//        AccountDTO createdAccount = coreClient.createAccount(accountDTO);
//        return ResponseEntity.ok(createdAccount);
//    }
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
//    /**
//     * Закрыть счет (мягкое удаление)
//     */
//    @DeleteMapping("/{accountId}")
//    public ResponseEntity<Void> closeAccount(
//            @PathVariable Long accountId,
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to close account: {}", accountId);
//
//        // Проверяем принадлежность счета
//        AccountDTO existingAccount = coreClient.getAccountById(accountId);
//        if (!existingAccount.getUserId().equals(userId)) {
//            return ResponseEntity.status(403).build();
//        }
//
//        // Проверяем нулевой баланс перед закрытием
//        if (existingAccount.getBalance().compareTo(BigDecimal.ZERO) != 0) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        coreClient.deleteAccount(accountId);
//        return ResponseEntity.noContent().build();
//    }
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
//    // ============= Операции со счетами =============
//
//    /**
//     * Внести деньги на счет
//     */
//    @PostMapping("/{accountId}/deposit")
//    public ResponseEntity<OperationResponse> deposit(
//            @PathVariable Long accountId,
//            @RequestParam BigDecimal amount,
//            @RequestParam(required = false) String description,
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to deposit {} to account {} for user {}", amount, accountId, userId);
//
//        // Проверяем принадлежность счета
//        AccountDTO account = coreClient.getAccountById(accountId);
//        if (!account.getUserId().equals(userId)) {
//            return ResponseEntity.status(403).build();
//        }
//
//        CreateOperationRequest request = CreateOperationRequest.builder()
//                .accountId(accountId)
//                .amount(amount)
//                .description(description != null ? description : "Deposit via user-service")
//                .operationType("DEPOSIT")
//                .build();
//
//        OperationResponse response = coreClient.deposit(request);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Снять деньги со счета
//     */
//    @PostMapping("/{accountId}/withdraw")
//    public ResponseEntity<OperationResponse> withdraw(
//            @PathVariable Long accountId,
//            @RequestParam BigDecimal amount,
//            @RequestParam(required = false) String description,
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to withdraw {} from account {} for user {}", amount, accountId, userId);
//
//        // Проверяем принадлежность счета
//        AccountDTO account = coreClient.getAccountById(accountId);
//        if (!account.getUserId().equals(userId)) {
//            return ResponseEntity.status(403).build();
//        }
//
//        // Проверяем достаточно ли средств
//        if (account.getBalance().compareTo(amount) < 0) {
//            return ResponseEntity.badRequest().body(
//                    OperationResponse.builder()
//                            .message("Insufficient funds")
//                            .build()
//            );
//        }
//
//        CreateOperationRequest request = CreateOperationRequest.builder()
//                .accountId(accountId)
//                .amount(amount)
//                .description(description != null ? description : "Withdrawal via user-service")
//                .operationType("WITHDRAWAL")
//                .build();
//
//        OperationResponse response = coreClient.withdraw(request);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Перевести деньги между счетами
//     */
//    @PostMapping("/transfer")
//    public ResponseEntity<OperationResponse> transfer(
//            @RequestBody TransferRequest request,
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to transfer {} from account {} to account {}",
//                request.getAmount(), request.getFromAccountId(), request.getToAccountId());
//
//        // Проверяем принадлежность счета отправителя
//        AccountDTO fromAccount = coreClient.getAccountById(request.getFromAccountId());
//        if (!fromAccount.getUserId().equals(userId)) {
//            return ResponseEntity.status(403).body(
//                    OperationResponse.builder()
//                            .message("You don't have permission to transfer from this account")
//                            .build()
//            );
//        }
//
//        // Проверяем достаточно ли средств
//        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
//            return ResponseEntity.badRequest().body(
//                    OperationResponse.builder()
//                            .message("Insufficient funds")
//                            .build()
//            );
//        }
//
//        TransferRequest transferRequest = TransferRequest.builder()
//                .fromAccountId(request.getFromAccountId())
//                .toAccountId(request.getToAccountId())
//                .amount(request.getAmount())
//                .description(request.getDescription())
//                .build();
//
//        OperationResponse response = coreClient.transfer(transferRequest);
//        return ResponseEntity.ok(response);
//    }
//
//    // ============= Операции (история) =============
//
//    /**
//     * Получить операции по счету
//     */
//    @GetMapping("/{accountId}/operations")
//    public ResponseEntity<List<OperationDTO>> getAccountOperations(
//            @PathVariable Long accountId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @RequestHeader("X-User-Id") Long userId) {
//        log.info("REST request to get operations for account: {}", accountId);
//
//        // Проверяем принадлежность счета
//        AccountDTO account = coreClient.getAccountById(accountId);
//        if (!account.getUserId().equals(userId)) {
//            return ResponseEntity.status(403).build();
//        }
//
//        List<OperationDTO> operations = coreClient.getAccountOperations(accountId, page, size);
//        return ResponseEntity.ok(operations);
//    }
//
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
