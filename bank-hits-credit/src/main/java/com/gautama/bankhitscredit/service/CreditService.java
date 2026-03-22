package com.gautama.bankhitscredit.service;


import com.gautama.bankhitscredit.client.AccountServiceClient;
import com.gautama.bankhitscredit.dto.*;
import com.gautama.bankhitscredit.entity.Credit;
import com.gautama.bankhitscredit.entity.CreditPayment;
import com.gautama.bankhitscredit.entity.CreditTariff;
import com.gautama.bankhitscredit.enums.CreditStatus;
import com.gautama.bankhitscredit.enums.PaymentStatus;
import com.gautama.bankhitscredit.mapper.CreditMapper;
import com.gautama.bankhitscredit.repository.CreditPaymentRepository;
import com.gautama.bankhitscredit.repository.CreditRepository;
import com.gautama.bankhitscredit.repository.CreditTariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;
    private final CreditPaymentRepository paymentRepository;
    private final CreditTariffRepository tariffRepository;
    private final CreditMapper creditMapper;
    private final AccountServiceClient accountServiceClient;

    @Value("${bank.master-account-number}")
    private String masterAccountNumber;

    // =================== КРЕДИТЫ ===================

    @Transactional(readOnly = true)
    public List<CreditResponse> getAllCredits() {
        return creditMapper.toCreditResponseList(
                creditRepository.findAll()
        );
    }

    @Transactional(readOnly = true)
    public List<CreditResponse> getClientCredits(UUID clientId) {
        return creditMapper.toCreditResponseList(
                creditRepository.findByClientId(clientId)
        );
    }

    @Transactional(readOnly = true)
    public CreditResponse getCredit(UUID id) {
        return creditMapper.toCreditResponse(findById(id));
    }

    public CreditResponse takeCredit(TakeCreditRequest request) {
        CreditTariff tariff = tariffRepository
                .findById(request.getTariffId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Тариф не найден: " + request.getTariffId()
                ));

        // проверяем что счёт клиента существует и активен
        AccountDTO clientAccount = accountServiceClient
                .getAccountByNumber(request.getAccountNumber());

        if (!"ACTIVE".equals(clientAccount.getStatus())) {
            throw new IllegalStateException(
                    "Счёт клиента закрыт или заблокирован"
            );
        }

        // проверяем баланс мастер-счёта
        AccountDTO masterAccount = accountServiceClient
                .getAccountByNumber(masterAccountNumber);

        if (masterAccount.getBalance()
                .compareTo(request.getAmount()) < 0) {
            throw new IllegalStateException(
                    "Недостаточно средств на мастер-счёте банка " +
                            "для выдачи кредита"
            );
        }

        // шаг 1 — списываем с мастер-счёта банка
        accountServiceClient.withdraw(
                new CreateOperationRequest(
                        masterAccountNumber,
                        "WITHDRAWAL",
                        request.getAmount(),
                        "Выдача кредита клиенту, тариф: " + tariff.getName()
                )
        );

        // шаг 2 — зачисляем на счёт клиента
        accountServiceClient.deposit(
                new CreateOperationRequest(
                        request.getAccountNumber(),
                        "DEPOSIT",
                        request.getAmount(),
                        "Выдача кредита по тарифу " + tariff.getName()
                )
        );

        Credit credit = Credit.builder()
                .clientId(request.getClientId())
                .accountNumber(request.getAccountNumber())
                .tariff(tariff)
                .principalAmount(request.getAmount())
                .remainingDebt(request.getAmount())
                .issuedAt(LocalDateTime.now())
                .status(CreditStatus.ACTIVE)
                .nextPaymentAt(LocalDateTime.now().plusMinutes(1))
                .build();

        return creditMapper.toCreditResponse(
                creditRepository.save(credit)
        );
    }

    public CreditResponse repayFull(UUID creditId) {
        Credit credit = findById(creditId);

        if (credit.getStatus() == CreditStatus.CLOSED) {
            throw new IllegalStateException("Кредит уже закрыт");
        }

        // шаг 1 — списываем со счёта клиента
        accountServiceClient.withdraw(
                new CreateOperationRequest(
                        credit.getAccountNumber(),
                        "WITHDRAWAL",
                        credit.getRemainingDebt(),
                        "Полное погашение кредита " + creditId
                )
        );

        // шаг 2 — возвращаем на мастер-счёт банка
        accountServiceClient.deposit(
                new CreateOperationRequest(
                        masterAccountNumber,
                        "DEPOSIT",
                        credit.getRemainingDebt(),
                        "Возврат по кредиту " + creditId
                )
        );

        savePayment(
                credit, credit.getRemainingDebt(), PaymentStatus.PAID
        );

        credit.setRemainingDebt(BigDecimal.ZERO);
        credit.setStatus(CreditStatus.CLOSED);
        credit.setClosedAt(LocalDateTime.now());

        return creditMapper.toCreditResponse(
                creditRepository.save(credit)
        );
    }

    public CreditResponse repayPartial(
            UUID creditId, PartialRepayRequest request) {

        Credit credit = findById(creditId);

        if (credit.getStatus() == CreditStatus.CLOSED) {
            throw new IllegalStateException("Кредит уже закрыт");
        }

        BigDecimal amount = request.getAmount()
                .min(credit.getRemainingDebt());

        // шаг 1 — списываем со счёта клиента
        accountServiceClient.withdraw(
                new CreateOperationRequest(
                        credit.getAccountNumber(),
                        "WITHDRAWAL",
                        amount,
                        "Частичное погашение кредита " + creditId
                )
        );

        // шаг 2 — возвращаем на мастер-счёт банка
        accountServiceClient.deposit(
                new CreateOperationRequest(
                        masterAccountNumber,
                        "DEPOSIT",
                        amount,
                        "Частичный возврат по кредиту " + creditId
                )
        );

        savePayment(credit, amount, PaymentStatus.PAID);

        credit.setRemainingDebt(
                credit.getRemainingDebt().subtract(amount)
        );

        if (credit.getRemainingDebt()
                .compareTo(BigDecimal.ZERO) <= 0) {
            credit.setStatus(CreditStatus.CLOSED);
            credit.setClosedAt(LocalDateTime.now());
        }

        return creditMapper.toCreditResponse(
                creditRepository.save(credit)
        );
    }

    @Transactional(readOnly = true)
    public List<CreditPaymentResponse> getPayments(UUID creditId) {
        findById(creditId); // проверяем что кредит существует
        return creditMapper.toPaymentResponseList(
                paymentRepository.findByCreditIdOrderByDueAtDesc(creditId)
        );
    }

    // =================== ТАРИФЫ ===================

    @Transactional(readOnly = true)
    public List<TariffResponse> getAllTariffs() {
        return creditMapper.toTariffResponseList(
                tariffRepository.findAll()
        );
    }

    public TariffResponse createTariff(CreateTariffRequest request) {
        if (tariffRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalStateException(
                    "Тариф с именем " + request.getName() + " уже существует"
            );
        }

        CreditTariff tariff = CreditTariff.builder()
                .name(request.getName())
                .annualRate(request.getAnnualRate())
                .termDays(request.getTermDays())
                .build();

        return creditMapper.toTariffResponse(
                tariffRepository.save(tariff)
        );
    }

    // =================== ВСПОМОГАТЕЛЬНЫЕ ===================

    private Credit findById(UUID id) {
        return creditRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Кредит не найден: " + id
                ));
    }

    void savePayment(
            Credit credit,
            BigDecimal amount,
            PaymentStatus status) {

        CreditPayment payment = CreditPayment.builder()
                .credit(credit)
                .amount(amount)
                .dueAt(LocalDateTime.now())
                .paidAt(status == PaymentStatus.PAID
                        ? LocalDateTime.now() : null)
                .status(status)
                .build();

        paymentRepository.save(payment);
    }
}