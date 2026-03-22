package com.gautama.bankhitscredit.service;

import com.gautama.bankhitscredit.client.AccountServiceClient;
import com.gautama.bankhitscredit.dto.CreateOperationRequest;
import com.gautama.bankhitscredit.entity.Credit;
import com.gautama.bankhitscredit.enums.CreditStatus;
import com.gautama.bankhitscredit.enums.PaymentStatus;
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
                // не смогли списать — помечаем просроченным
                credit.setStatus(CreditStatus.OVERDUE);
                creditRepository.save(credit);

                // сохраняем просроченный платёж
                creditService.savePayment(
                        credit,
                        calculatePaymentAmount(credit),
                        PaymentStatus.OVERDUE
                );
            }
        }
    }

//    try {
//        // шаг 1 — списываем с мастер-счёта
//        accountServiceClient.withdraw(...masterAccount...);
//
//        try {
//            // шаг 2 — зачисляем клиенту
//            accountServiceClient.deposit(...clientAccount...);
//        } catch (Exception e) {
//            // компенсация — возвращаем на мастер-счёт
//            accountServiceClient.deposit(...masterAccount...);
//            throw e;
//        }
//    } catch (Exception e) {
//        throw new IllegalStateException(
//                "Ошибка выдачи кредита: " + e.getMessage()
//        );
//    }

    private void processPayment(Credit credit) {
        BigDecimal paymentAmount = calculatePaymentAmount(credit);
        paymentAmount = paymentAmount.min(credit.getRemainingDebt());

        // шаг 1 — списываем со счёта клиента
        accountServiceClient.withdraw(
                new CreateOperationRequest(
                        credit.getAccountNumber(),
                        "WITHDRAWAL",
                        paymentAmount,
                        "Плановый платёж по кредиту " + credit.getId()
                )
        );

        // шаг 2 — зачисляем на мастер-счёт банка
        accountServiceClient.deposit(
                new CreateOperationRequest(
                        masterAccountNumber,
                        "DEPOSIT",
                        paymentAmount,
                        "Плановый возврат по кредиту " + credit.getId()
                )
        );

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

        // ежедневный процент
        BigDecimal dailyRate = credit.getTariff().getAnnualRate()
                .divide(
                        BigDecimal.valueOf(365 * 100),
                        10,
                        RoundingMode.HALF_UP
                );

        // начисленные проценты за период
        BigDecimal interest = credit.getRemainingDebt()
                .multiply(dailyRate)
                .setScale(2, RoundingMode.HALF_UP);

        // тело долга за период (равными частями)
        BigDecimal principal = credit.getPrincipalAmount()
                .divide(
                        BigDecimal.valueOf(termDays),
                        2,
                        RoundingMode.HALF_UP
                );

        return interest.add(principal);
    }
}