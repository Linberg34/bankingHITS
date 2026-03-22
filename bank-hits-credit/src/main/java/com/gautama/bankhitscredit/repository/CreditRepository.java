package com.gautama.bankhitscredit.repository;

import com.gautama.bankhitscredit.entity.Credit;
import com.gautama.bankhitscredit.enums.CreditStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CreditRepository extends JpaRepository<Credit, UUID> {
    List<Credit> findByClientId(UUID clientId);
    List<Credit> findByStatus(CreditStatus status);

    List<Credit> findByClientIdAndStatus(UUID clientId, CreditStatus status);

    // для планировщика — все активные кредиты у которых подошёл срок
    @Query("""
        SELECT c FROM Credit c
        WHERE c.status = 'ACTIVE'
        AND c.nextPaymentAt <= :now
        """)
    List<Credit> findDueCredits(
            @Param("now") LocalDateTime now
    );

    int countByClientIdAndStatus(UUID clientId, CreditStatus status);
}
