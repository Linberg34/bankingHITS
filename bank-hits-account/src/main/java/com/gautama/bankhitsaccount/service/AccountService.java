package com.gautama.bankhitsaccount.service;

import com.gautama.bankhitsaccount.dto.AccountDTO;
import com.gautama.bankhitsaccount.mapper.AccountMapper;
import com.gautama.bankhitsaccount.model.Account;
import com.gautama.bankhitsaccount.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

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
            account.setStatus("ACTIVE");
        }

        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with id: {}", savedAccount.getId());

        return accountMapper.toDTO(savedAccount);
    }

    @Transactional
    public AccountDTO createAccountCurrent(Long userId) {
        AccountDTO accountDTO = new AccountDTO(userId, generateAccountNumber(), BigDecimal.ZERO, "ACTIVE");
        if (accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())) {
            throw new RuntimeException("Account number already exists: " + accountDTO.getAccountNumber());
        }

        Account account = accountMapper.toEntity(accountDTO);

        // Установка значений по умолчанию
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        if (account.getStatus() == null) {
            account.setStatus("ACTIVE");
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
    public void deleteAccount(Long id) {
        log.info("Deleting account with id: {}", id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        // Мягкое удаление - меняем статус
        account.setStatus("CLOSED");
        accountRepository.save(account);
    }

    // Вспомогательный метод для генерации номера счета
    public String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Формат: 40817XXXXXXXXXXXX (российский счет)
        sb.append("40817");

        for (int i = 0; i < 15; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}