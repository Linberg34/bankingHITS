package com.gautama.bankhitsaccount.repository;

import com.gautama.bankhitsaccount.model.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    List<Operation> findByAccountId(Long accountId);

    Page<Operation> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    List<Operation> findByAccountIdAndCreatedAtBetween(Long accountId, LocalDateTime start, LocalDateTime end);

    List<Operation> findByAccountIdAndOperationType(Long accountId, String operationType);

    @Query("SELECT o FROM Operation o WHERE o.account.userId = :userId ORDER BY o.createdAt DESC")
    List<Operation> findByUserId(@Param("userId") Long userId);

    @Query("SELECT o FROM Operation o WHERE o.account.id = :accountId AND o.operationType = :type")
    List<Operation> findByAccountIdAndType(@Param("accountId") Long accountId, @Param("type") String type);

    @Query("SELECT SUM(o.amount) FROM Operation o WHERE o.account.id = :accountId AND o.operationType = 'DEPOSIT'")
    BigDecimal getTotalDeposits(@Param("accountId") Long accountId);

    @Query("SELECT SUM(o.amount) FROM Operation o WHERE o.account.id = :accountId AND o.operationType = 'WITHDRAWAL'")
    BigDecimal getTotalWithdrawals(@Param("accountId") Long accountId);
}