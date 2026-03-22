package com.gautama.bankhitscredit.service;

import com.gautama.bankhitscredit.dto.CreditRatingResponse;
import com.gautama.bankhitscredit.entity.Credit;
import com.gautama.bankhitscredit.enums.CreditStatus;
import com.gautama.bankhitscredit.enums.PaymentStatus;
import com.gautama.bankhitscredit.repository.CreditPaymentRepository;
import com.gautama.bankhitscredit.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final CreditRepository creditRepository;
    private final CreditPaymentRepository paymentRepository;

    public CreditRatingResponse calculateRating(UUID clientId) {
        List<Credit> credits = creditRepository.findByClientId(clientId);

        int totalCredits = credits.size();

        int activeCredits = (int) credits.stream()
                .filter(c -> c.getStatus() == CreditStatus.ACTIVE)
                .count();

        int closedCredits = (int) credits.stream()
                .filter(c -> c.getStatus() == CreditStatus.CLOSED)
                .count();

        int overdueCredits = (int) credits.stream()
                .filter(c -> c.getStatus() == CreditStatus.OVERDUE)
                .count();

        // просроченные платежи по всем кредитам клиента
        List<UUID> creditIds = credits.stream()
                .map(Credit::getId)
                .toList();

        int overduePayments = creditIds.isEmpty() ? 0 :
                paymentRepository.countByCredit_IdInAndStatus(
                        creditIds,
                        PaymentStatus.OVERDUE
                );

        // считаем score
        int score = 100;
        score -= overduePayments * 10;   // -10 за каждый просроченный платёж
        score -= overdueCredits * 15;    // -15 за каждый просроченный кредит
        score += closedCredits * 5;      // +5 за каждый закрытый кредит

        // бонус если нет ни одного просроченного платежа
        if (overduePayments == 0 && totalCredits > 0) {
            score += 10;
        }

        score = Math.max(0, Math.min(100, score));

        return new CreditRatingResponse(
                clientId,
                score,
                overduePayments,
                totalCredits,
                activeCredits,
                closedCredits,
                labelFor(score),
                LocalDateTime.now()
        );
    }

    private String labelFor(int score) {
        if (score >= 80) return "Отличный";
        if (score >= 60) return "Хороший";
        if (score >= 40) return "Удовлетворительный";
        if (score >= 20) return "Плохой";
        return "Очень плохой";
    }
}