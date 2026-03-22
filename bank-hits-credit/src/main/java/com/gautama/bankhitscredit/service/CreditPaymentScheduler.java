package com.gautama.bankhitscredit.service;

import com.gautama.bankhitscredit.client.AccountServiceClient;
import com.gautama.bankhitscredit.dto.AccountDTO;
import com.gautama.bankhitscredit.entity.Credit;
import com.gautama.bankhitscredit.enums.CreditStatus;
import com.gautama.bankhitscredit.enums.PaymentStatus;
import com.gautama.bankhitscredit.kafka.CreditOperationProducer;
import com.gautama.bankhitscredit.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditPaymentScheduler {

    private final CreditRepository creditRepository;
    private final CreditService creditService;
    private final CreditOperationProducer operationProducer;
    private final AccountServiceClient accountServiceClient;

    @Value("${bank.master-account-number}")
    private String masterAccountNumber;

    // раз в минуту для тестирования
    // в проде заменить на @Scheduled(cron = "0 0 0 * * *") — раз в день
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void processScheduledPayments() {
        List<Credit> dueCredits =
                creditRepository.findDueCredits(LocalDateTime.now());

        log.info(
                "Планировщик: найдено {} кредитов для списания",
                dueCredits.size()
        );

        for (Credit credit : dueCredits) {
            try {
                processPayment(credit);
            } catch (Exception e) {
                log.error(
                        "Ошибка списания по кредиту {}: {}",
                        credit.getId(), e.getMessage()
                );
                credit.setStatus(CreditStatus.OVERDUE);
                creditRepository.save(credit);

                creditService.savePayment(
                        credit,
                        calculatePaymentAmount(credit),
                        PaymentStatus.OVERDUE
                );
            }
        }
    }

    private void processPayment(Credit credit) {
        BigDecimal paymentAmount = calculatePaymentAmount(credit);
        paymentAmount = paymentAmount.min(credit.getRemainingDebt());

        AccountDTO account = accountServiceClient.getAccountByNumber(credit.getAccountNumber());
        if (account.getBalance().compareTo(paymentAmount) < 0) {
            throw new IllegalStateException(
                    "Недостаточно средств на счёте " + credit.getAccountNumber() +
                    ": баланс " + account.getBalance() + ", требуется " + paymentAmount
            );
        }

        operationProducer.sendWithdraw(credit.getAccountNumber(), paymentAmount, credit.getClientId());
        operationProducer.sendDeposit(masterAccountNumber, paymentAmount, credit.getClientId());

        credit.setRemainingDebt(
                credit.getRemainingDebt().subtract(paymentAmount)
        );

        creditService.savePayment(
                credit, paymentAmount, PaymentStatus.PAID
        );

        if (credit.getRemainingDebt()
                .compareTo(BigDecimal.ZERO) <= 0) {
            credit.setStatus(CreditStatus.CLOSED);
            credit.setClosedAt(LocalDateTime.now());
            log.info("Кредит {} полностью погашен", credit.getId());
        } else {
            credit.setNextPaymentAt(
                    LocalDateTime.now().plusMinutes(1)
            );
        }

        creditRepository.save(credit);
    }

    private BigDecimal calculatePaymentAmount(Credit credit) {
        int termDays = credit.getTariff().getTermDays();

        BigDecimal dailyRate = credit.getTariff().getAnnualRate()
                .divide(
                        BigDecimal.valueOf(365 * 100),
                        10,
                        RoundingMode.HALF_UP
                );

        BigDecimal interest = credit.getRemainingDebt()
                .multiply(dailyRate)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal principal = credit.getPrincipalAmount()
                .divide(
                        BigDecimal.valueOf(termDays),
                        2,
                        RoundingMode.HALF_UP
                );

        return interest.add(principal);
    }
}