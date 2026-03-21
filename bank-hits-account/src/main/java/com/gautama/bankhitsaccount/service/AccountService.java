package com.gautama.bankhitsaccount.service;

import com.gautama.bankhitsaccount.dto.AccountDTO;
import com.gautama.bankhitsaccount.dto.AccountListRequest;
import com.gautama.bankhitsaccount.mapper.AccountMapper;
import com.gautama.bankhitsaccount.model.AccountCurrency;
import com.gautama.bankhitsaccount.model.Account;
import com.gautama.bankhitsaccount.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountService {
    private static final String ACTIVE_STATUS = "ACTIVE";

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Value("${bank.master-account.number:00000000000000000001}")
    private String masterAccountNumber;

    @Value("${bank.master-account.client-id:0}")
    private Long masterAccountClientId;

    @Value("${bank.master-account.initial-balance:1000000.00}")
    private BigDecimal masterAccountInitialBalance;

    @Value("${bank.default-currency:RUB}")
    private String defaultCurrency;

    @PostConstruct
    @Transactional
    public void ensureMasterAccountExists() {
        if (accountRepository.existsByAccountNumber(masterAccountNumber)) {
            return;
        }

        Account masterAccount = new Account();
        masterAccount.setClientId(masterAccountClientId);
        masterAccount.setAccountNumber(masterAccountNumber);
        masterAccount.setBalance(masterAccountInitialBalance);
        masterAccount.setCurrency(resolveCurrency(defaultCurrency));
        masterAccount.setStatus(ACTIVE_STATUS);
        accountRepository.save(masterAccount);

        log.info("Master account created with number {}", masterAccountNumber);
    }

    public List<AccountDTO> getAccountsByUserId(Long userId) {
        log.info("Fetching accounts for user: {}", userId);
        return accountRepository.findByClientId(userId)
                .stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }

    public AccountDTO getAccountById(Long id) {
        log.info("Fetching account by id: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return accountMapper.toDTO(account);
    }

    public AccountDTO getAccountByNumber(String accountNumber) {
        log.info("Fetching account by number: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));
        return accountMapper.toDTO(account);
    }

    @Transactional
    public AccountDTO createAccount(AccountDTO accountDTO) {
        log.info("Creating new account for user: {}", accountDTO.getClientId());

        // Проверка на уникальность номера счета
        if (accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())) {
            throw new RuntimeException("Account number already exists: " + accountDTO.getAccountNumber());
        }

        Account account = accountMapper.toEntity(accountDTO);

        // Установка значений по умолчанию
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        if (account.getStatus() == null) {
            account.setStatus(ACTIVE_STATUS);
        }
        if (account.getCurrency() == null) {
            account.setCurrency(resolveCurrency(accountDTO.getCurrency()));
        }

        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with id: {}", savedAccount.getId());

        return accountMapper.toDTO(savedAccount);
    }

    @Transactional
    public AccountDTO createAccountCurrent(Long userId, String currency) {
        AccountDTO accountDTO = new AccountDTO(
                userId,
                generateAccountNumber(),
                BigDecimal.ZERO,
                resolveCurrency(currency).name(),
                ACTIVE_STATUS
        );
        if (accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())) {
            throw new RuntimeException("Account number already exists: " + accountDTO.getAccountNumber());
        }

        Account account = accountMapper.toEntity(accountDTO);

        // Установка значений по умолчанию
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        if (account.getStatus() == null) {
            account.setStatus(ACTIVE_STATUS);
        }
        if (account.getCurrency() == null) {
            account.setCurrency(resolveCurrency(accountDTO.getCurrency()));
        }

        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with id: {}", savedAccount.getId());

        return accountMapper.toDTO(savedAccount);
    }

    @Transactional
    public AccountDTO updateAccount(Long id, AccountDTO accountDTO) {
        log.info("Updating account with id: {}", id);

        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        // Обновляем только разрешенные поля
        if (accountDTO.getStatus() != null) {
            existingAccount.setStatus(accountDTO.getStatus());
        }

        Account updatedAccount = accountRepository.save(existingAccount);
        log.info("Account updated successfully with id: {}", updatedAccount.getId());

        return accountMapper.toDTO(updatedAccount);
    }

    @Transactional
    public void updateBalance(Long accountId, BigDecimal amount) {
        log.info("Updating balance for account: {}, amount: {}", accountId, amount);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        BigDecimal newBalance = account.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    @Transactional
    public Account getMasterAccountForUpdate() {
        return accountRepository.findByAccountNumberForUpdate(masterAccountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Master account not found: " + masterAccountNumber));
    }

    public String getMasterAccountNumber() {
        return masterAccountNumber;
    }

    public AccountCurrency resolveCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return AccountCurrency.valueOf(defaultCurrency.toUpperCase());
        }

        try {
            return AccountCurrency.valueOf(currency.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unsupported currency: " + currency + ". Supported: RUB, USD, EUR");
        }
    }

    @Transactional
    public void deleteAccount(String accountNumber) {
        log.info("Deleting account with accountNumber: {}", accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with accountNumber: " + accountNumber));

        // Мягкое удаление - меняем статус
        account.setStatus("CLOSED");
        accountRepository.save(account);
    }

    // Вспомогательный метод для генерации номера счета
    public String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Держим длину в пределах signed long для совместимости с соседними сервисами,
        // которые местами все еще пытаются читать номер счета как число.
        sb.append("40817");

        for (int i = 0; i < 14; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    public Page<AccountDTO> getAllAccounts(AccountListRequest request) {
        log.info("Fetching all accounts with filters: {}", request);

        Page<Account> accounts = accountRepository.findWithFilters(
                request.getUserId(),
                request.getStatus(),
                request.getMinBalance(),
                request.getMaxBalance(),
                request.getPageable()
        );

        return accounts.map(accountMapper::toDTO);
    }

}
