package com.gautama.bankhitscredit.service;

import com.gautama.bankhitscredit.client.CoreServiceClient;
import com.gautama.bankhitscredit.entity.Credit;
import com.gautama.bankhitscredit.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditPaymentScheduler {

    private final CreditRepository creditRepository;
    private final CoreServiceClient coreClient;

    @Scheduled(cron = "${credit.payment.cron}")
    @Transactional
    public void processPayments() {
        List<Credit> activeCredits = creditRepository.findByStatus(Credit.CreditStatus.ACTIVE);
        log.info("Обработка платежей: {} активных кредитов", activeCredits.size());

        for (Credit credit : activeCredits) {
            processPayment(credit);
        }
    }

    private void processPayment(Credit credit) {
        // Считаем платёж за период:
        // Если тестируем раз в минуту — считаем "минутную" ставку из годовой
        // annualRate / 100 / 525600 минут в году
        BigDecimal periodRate = credit.getTariff().getAnnualRate()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(525_600), 10, RoundingMode.HALF_UP);

        BigDecimal payment = credit.getRemainingDebt()
                .multiply(periodRate)
                .setScale(2, RoundingMode.HALF_UP);

        // Минимальный платёж — 1 копейка, чтобы долг всегда уменьшался
        if (payment.compareTo(BigDecimal.valueOf(0.01)) < 0) {
            payment = BigDecimal.valueOf(0.01);
        }

        // Не списываем больше чем остаток долга
        if (payment.compareTo(credit.getRemainingDebt()) > 0) {
            payment = credit.getRemainingDebt();
        }

        boolean success = coreClient.tryWithdraw(credit.getAccountId(), payment);

        if (success) {
            credit.setRemainingDebt(credit.getRemainingDebt().subtract(payment));
            if (credit.getRemainingDebt().compareTo(BigDecimal.ZERO) == 0) {
                credit.setStatus(Credit.CreditStatus.CLOSED);
                credit.setClosedAt(LocalDateTime.now());
                log.info("Кредит {} полностью погашен", credit.getId());
            }
        } else {
            // Не удалось списать — помечаем как просроченный
            credit.setStatus(Credit.CreditStatus.OVERDUE);
            log.warn("Кредит {} переведён в статус OVERDUE (недостаточно средств)", credit.getId());
        }

        creditRepository.save(credit);
    }
}