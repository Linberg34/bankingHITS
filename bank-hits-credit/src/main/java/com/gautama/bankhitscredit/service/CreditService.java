package com.gautama.bankhitscredit.service;


import com.gautama.bankhitscredit.client.AccountServiceClient;
import com.gautama.bankhitscredit.dto.*;
import com.gautama.bankhitscredit.entity.Credit;
import com.gautama.bankhitscredit.entity.CreditPayment;
import com.gautama.bankhitscredit.entity.CreditTariff;
import com.gautama.bankhitscredit.enums.CreditStatus;
import com.gautama.bankhitscredit.enums.PaymentStatus;
import com.gautama.bankhitscredit.kafka.CreditOperationProducer;
import com.gautama.bankhitscredit.mapper.CreditMapper;
import com.gautama.bankhitscredit.repository.CreditPaymentRepository;
import com.gautama.bankhitscredit.repository.CreditRepository;
import com.gautama.bankhitscredit.repository.CreditTariffRepository;
import com.gautama.bankhitscredit.infrastructure.resilience.DownstreamCallExecutor;
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
    private final CreditOperationProducer operationProducer;
    private final DownstreamCallExecutor downstreamCallExecutor;

    @Value("${bank.master-account-number}")
    private String masterAccountNumber;

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

        AccountDTO clientAccount = callCore(
                "GET",
                "/internal/accounts/number/" + request.getAccountNumber(),
                200,
                () -> accountServiceClient.getAccountByNumber(request.getAccountNumber())
        );

        if (!"ACTIVE".equals(clientAccount.getStatus())) {
            throw new IllegalStateException(
                    "Счёт клиента закрыт или заблокирован"
            );
        }

        AccountDTO masterAccount = callCore(
                "GET",
                "/internal/accounts/number/" + masterAccountNumber,
                200,
                () -> accountServiceClient.getAccountByNumber(masterAccountNumber)
        );

        if (masterAccount.getBalance()
                .compareTo(request.getAmount()) < 0) {
            throw new IllegalStateException(
                    "Недостаточно средств на мастер-счёте банка " +
                            "для выдачи кредита"
            );
        }

        operationProducer.sendWithdraw(masterAccountNumber, request.getAmount(), request.getClientId());
        operationProducer.sendDeposit(request.getAccountNumber(), request.getAmount(), request.getClientId());

        Credit credit = Credit.builder()
                .clientId(request.getClientId())
                .accountNumber(request.getAccountNumber())
                .currency(clientAccount.getCurrency())
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

        operationProducer.sendWithdraw(credit.getAccountNumber(), credit.getRemainingDebt(), credit.getClientId());
        operationProducer.sendDeposit(masterAccountNumber, credit.getRemainingDebt(), credit.getClientId());

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

        operationProducer.sendWithdraw(credit.getAccountNumber(), amount, credit.getClientId());
        operationProducer.sendDeposit(masterAccountNumber, amount, credit.getClientId());

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
        findById(creditId);
        return creditMapper.toPaymentResponseList(
                paymentRepository.findByCreditIdOrderByDueAtDesc(creditId)
        );
    }

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

    private <T> T callCore(String method, String path, int successStatus, java.util.function.Supplier<T> action) {
        return downstreamCallExecutor.execute(
                DownstreamCallExecutor.DownstreamService.CORE,
                method,
                path,
                successStatus,
                action
        );
    }
}
