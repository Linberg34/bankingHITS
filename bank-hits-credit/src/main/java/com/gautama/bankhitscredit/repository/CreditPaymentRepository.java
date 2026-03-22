package com.gautama.bankhitscredit.repository;

import com.gautama.bankhitscredit.entity.CreditPayment;
import com.gautama.bankhitscredit.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CreditPaymentRepository extends JpaRepository<CreditPayment, UUID> {
    List<CreditPayment> findByCreditId(UUID creditId);

    List<CreditPayment> findByCreditIdOrderByDueAtDesc(UUID creditId);

    int countByCredit_IdInAndStatus(
            List<UUID> creditIds,
            PaymentStatus status
    );

    int countByCreditClientIdAndStatus(
            UUID clientId,
            PaymentStatus status
    );
}
