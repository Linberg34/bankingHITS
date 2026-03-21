package com.gautama.bankhitsaccount.service;

import com.gautama.bankhitsaccount.dto.CreateOperationRequest;
import com.gautama.bankhitsaccount.dto.OperationDTO;
import com.gautama.bankhitsaccount.dto.OperationResponse;
import com.gautama.bankhitsaccount.mapper.OperationMapper;
import com.gautama.bankhitsaccount.model.Account;
import com.gautama.bankhitsaccount.model.Operation;
import com.gautama.bankhitsaccount.repository.AccountRepository;
import com.gautama.bankhitsaccount.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OperationService {
    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final String OPERATION_DEPOSIT = "DEPOSIT";
    private static final String OPERATION_WITHDRAWAL = "WITHDRAWAL";
    private static final String OPERATION_CREDIT_ISSUE = "CREDIT_ISSUE";

    private final OperationRepository operationRepository;
    private final AccountRepository accountRepository;
    private final OperationMapper operationMapper;
    private final AccountService accountService;

    @Transactional
    public OperationResponse deposit(CreateOperationRequest request) {
        log.info("Processing deposit: accountId={}, amount={}", request.getAccountNumber(), request.getAmount());

        if (isCreditDisbursement(request)) {
            return issueCreditFromMasterAccount(request);
        }

        Account account = accountRepository.findByAccountNumberForUpdate(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!ACTIVE_STATUS.equals(account.getStatus())) {
            throw new RuntimeException("Account is not active");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be positive");
        }

        // Создаем операцию
        Operation operation = operationMapper.toEntity(request, account.getAccountNumber());
        operation.setOperationType(OPERATION_DEPOSIT);
        operation.setBalanceBefore(account.getBalance());
        operation.setBalanceAfter(account.getBalance().add(request.getAmount()));
        operation.setStatus("SUCCESS");

        // Обновляем баланс счета
        account.setBalance(operation.getBalanceAfter());
//
//        // Генерируем transactionId если не указан
//        if (operation.getTransactionId() == null) {
//            operation.setTransactionId(generateTransactionId());
//        }

        accountRepository.save(account);
        Operation savedOperation = operationRepository.save(operation);

        log.info("Deposit completed successfully. New balance: {}", account.getBalance());

        return OperationResponse.builder()
                .operation(operationMapper.toDTO(savedOperation))
                .account(accountService.getAccountById(account.getId()))
                .message("Deposit successful")
                .build();
    }

    @Transactional
    public OperationResponse withdraw(CreateOperationRequest request) {
        log.info("Processing withdrawal: accountId={}, amount={}", request.getAccountNumber(), request.getAmount());

        Account account = accountRepository.findByAccountNumberForUpdate(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!ACTIVE_STATUS.equals(account.getStatus())) {
            throw new RuntimeException("Account is not active");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Withdrawal amount must be positive");
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds. Available: " + account.getBalance());
        }

        // Создаем операцию
        Operation operation = operationMapper.toEntity(request, account.getAccountNumber());
        operation.setOperationType(OPERATION_WITHDRAWAL);
        operation.setBalanceBefore(account.getBalance());
        operation.setBalanceAfter(account.getBalance().subtract(request.getAmount()));
        operation.setStatus("SUCCESS");

        // Обновляем баланс счета
        account.setBalance(operation.getBalanceAfter());

//        // Генерируем transactionId если не указан
//        if (operation.getTransactionId() == null) {
//            operation.setTransactionId(generateTransactionId());
//        }

        accountRepository.save(account);
        Operation savedOperation = operationRepository.save(operation);

        log.info("Withdrawal completed successfully. New balance: {}", account.getBalance());

        return OperationResponse.builder()
                .operation(operationMapper.toDTO(savedOperation))
                .account(accountService.getAccountById(account.getId()))
                .message("Withdrawal successful")
                .build();
    }

//    @Transactional
//    public OperationResponse transfer(TransferRequest request) {
//        log.info("Processing transfer: from={}, to={}, amount={}",
//                request.getFromAccountId(), request.getToAccountId(), request.getAmount());
//
//        if (request.getFromAccountId().equals(request.getToAccountId())) {
//            throw new RuntimeException("Cannot transfer to the same account");
//        }
//
//        Account fromAccount = accountRepository.findById(request.getFromAccountId())
//                .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));
//
//        Account toAccount = accountRepository.findById(request.getToAccountId())
//                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found"));
//
//        // Проверки
//        if (!"ACTIVE".equals(fromAccount.getStatus())) {
//            throw new RuntimeException("Source account is not active");
//        }
//        if (!"ACTIVE".equals(toAccount.getStatus())) {
//            throw new RuntimeException("Destination account is not active");
//        }
//
//        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
//            throw new RuntimeException("Transfer amount must be positive");
//        }
//
//        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
//            throw new RuntimeException("Insufficient funds in source account");
//        }
//
//        String transactionId = request.getTransactionId() != null ?
//                request.getTransactionId() : generateTransactionId();
//
//        // Операция списания
//        Operation withdrawalOp = new Operation();
//        withdrawalOp.setAccount(fromAccount);
//        withdrawalOp.setOperationType("TRANSFER_OUT");
//        withdrawalOp.setAmount(request.getAmount().negate());
//        withdrawalOp.setBalanceBefore(fromAccount.getBalance());
//        withdrawalOp.setBalanceAfter(fromAccount.getBalance().subtract(request.getAmount()));
//        withdrawalOp.setCurrency(fromAccount.getCurrency());
//        withdrawalOp.setStatus("SUCCESS");
//        withdrawalOp.setDescription(request.getDescription());
//        withdrawalOp.setTransactionId(transactionId);
//        withdrawalOp.setCounterpartyAccount(toAccount.getAccountNumber());
//        withdrawalOp.setCounterpartyName("Transfer to account " + toAccount.getAccountNumber());
//
//        // Операция зачисления
//        Operation depositOp = new Operation();
//        depositOp.setAccount(toAccount);
//        depositOp.setOperationType("TRANSFER_IN");
//        depositOp.setAmount(request.getAmount());
//        depositOp.setBalanceBefore(toAccount.getBalance());
//        depositOp.setBalanceAfter(toAccount.getBalance().add(request.getAmount()));
//        depositOp.setCurrency(toAccount.getCurrency());
//        depositOp.setStatus("SUCCESS");
//        depositOp.setDescription(request.getDescription());
//        depositOp.setTransactionId(transactionId);
//        depositOp.setCounterpartyAccount(fromAccount.getAccountNumber());
//        depositOp.setCounterpartyName("Transfer from account " + fromAccount.getAccountNumber());
//
//        // Обновляем балансы
//        fromAccount.setBalance(withdrawalOp.getBalanceAfter());
//        toAccount.setBalance(depositOp.getBalanceAfter());
//
//        // Сохраняем
//        accountRepository.save(fromAccount);
//        accountRepository.save(toAccount);
//        operationRepository.save(withdrawalOp);
//        operationRepository.save(depositOp);
//
//        log.info("Transfer completed successfully. Transaction ID: {}", transactionId);
//
//        return OperationResponse.builder()
//                .message("Transfer successful")
//                .build();
//    }

    public List<OperationDTO> getAccountOperations(String accountNumber, int page, int size) {
        log.info("Fetching operations for account: {}", accountNumber);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Operation> operations = operationRepository.findByAccountNumberOrderByCreatedAtDesc(accountNumber, pageable);
        return operationMapper.toDTOList(operations.getContent());
    }
    public List<OperationDTO> getAccountOperations(String accountNumber) {
        log.info("Fetching operations for account: {}", accountNumber);
        List<Operation> operations = operationRepository.findByAccountNumber(accountNumber);
        return operationMapper.toDTOList(operations);
    }

    public List<OperationDTO> getAccountOperationsByDateRange(
            String accountNumber, LocalDateTime start, LocalDateTime end) {
        log.info("Fetching operations for account {} between {} and {}", accountNumber, start, end);
        List<Operation> operations = operationRepository.findByAccountNumberAndCreatedAtBetween(accountNumber, start, end);
        return operationMapper.toDTOList(operations);
    }

    public OperationDTO getOperationById(Long id) {
        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operation not found"));
        return operationMapper.toDTO(operation);
    }

//    public List<OperationDTO> getOperationsByTransactionId(String transactionId) {
//        List<Operation> operations = operationRepository.findByTransactionId(transactionId);
//        return operationMapper.toDTOList(operations);
//    }

//    public OperationDTO getLastOperation(Long accountId) {
//        Pageable pageable = PageRequest.of(0, 1, Sort.by("createdAt").descending());
//        Page<Operation> operations = operationRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable);
//        if (operations.hasContent()) {
//            return operationMapper.toDTO(operations.getContent().get(0));
//        }
//        return null;
//    }

//    public BigDecimal getAccountTurnover(Long accountId, LocalDateTime start, LocalDateTime end) {
//        List<Operation> operations = operationRepository.findByAccountIdAndCreatedAtBetween(accountId, start, end);
//        return operations.stream()
//                .map(Operation::getAmount)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    private boolean isCreditDisbursement(CreateOperationRequest request) {
        if (request.getOperationType() != null && OPERATION_CREDIT_ISSUE.equalsIgnoreCase(request.getOperationType())) {
            return true;
        }

        if (request.getDescription() == null) {
            return false;
        }

        String normalizedDescription = request.getDescription().toLowerCase();
        return normalizedDescription.contains("кредитных средств")
                || normalizedDescription.contains("credit");
    }

    private OperationResponse issueCreditFromMasterAccount(CreateOperationRequest request) {
        Account masterAccount = accountService.getMasterAccountForUpdate();
        Account clientAccount = accountRepository.findByAccountNumberForUpdate(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!ACTIVE_STATUS.equals(masterAccount.getStatus())) {
            throw new RuntimeException("Master account is not active");
        }
        if (!ACTIVE_STATUS.equals(clientAccount.getStatus())) {
            throw new RuntimeException("Account is not active");
        }
        if (masterAccount.getAccountNumber().equals(clientAccount.getAccountNumber())) {
            throw new RuntimeException("Credit cannot be issued to master account");
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Credit amount must be positive");
        }
        if (masterAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds on master account. Available: " + masterAccount.getBalance());
        }

        BigDecimal masterBalanceBefore = masterAccount.getBalance();
        BigDecimal clientBalanceBefore = clientAccount.getBalance();

        masterAccount.setBalance(masterBalanceBefore.subtract(request.getAmount()));
        clientAccount.setBalance(clientBalanceBefore.add(request.getAmount()));

        Operation masterOperation = operationMapper.toEntity(request, masterAccount.getAccountNumber());
        masterOperation.setOperationType(OPERATION_WITHDRAWAL);
        masterOperation.setBalanceBefore(masterBalanceBefore);
        masterOperation.setBalanceAfter(masterAccount.getBalance());
        masterOperation.setStatus("SUCCESS");
        masterOperation.setDescription(buildMasterAccountDescription(request));

        Operation clientOperation = operationMapper.toEntity(request, clientAccount.getAccountNumber());
        clientOperation.setOperationType(OPERATION_CREDIT_ISSUE);
        clientOperation.setBalanceBefore(clientBalanceBefore);
        clientOperation.setBalanceAfter(clientAccount.getBalance());
        clientOperation.setStatus("SUCCESS");

        accountRepository.save(masterAccount);
        accountRepository.save(clientAccount);
        operationRepository.save(masterOperation);
        Operation savedClientOperation = operationRepository.save(clientOperation);

        log.info("Credit issued from master account {} to {} amount {}", masterAccount.getAccountNumber(), clientAccount.getAccountNumber(), request.getAmount());

        return OperationResponse.builder()
                .operation(operationMapper.toDTO(savedClientOperation))
                .account(accountService.getAccountById(clientAccount.getId()))
                .message("Credit issued successfully from master account")
                .build();
    }

    private String buildMasterAccountDescription(CreateOperationRequest request) {
        String baseDescription = request.getDescription() == null ? "Credit disbursement" : request.getDescription();
        return "Master account funding: " + baseDescription + ", beneficiary=" + request.getAccountNumber();
    }
}
