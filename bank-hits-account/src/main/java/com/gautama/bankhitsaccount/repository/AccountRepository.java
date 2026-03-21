package com.gautama.bankhitsaccount.repository;

import com.gautama.bankhitsaccount.model.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByClientId(Long clientId);

    Optional<Account> findByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    Page<Account> findAllByOrderByBalanceDesc(Pageable pageable);

    Page<Account> findAllByOrderByBalanceAsc(Pageable pageable);

    @Query("SELECT a FROM Account a WHERE " +
            "(:userId IS NULL OR a.clientId = :userId) AND " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:minBalance IS NULL OR a.balance >= :minBalance) AND " +
            "(:maxBalance IS NULL OR a.balance <= :maxBalance)")
    Page<Account> findWithFilters(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("minBalance") BigDecimal minBalance,
            @Param("maxBalance") BigDecimal maxBalance,
            Pageable pageable);
}
