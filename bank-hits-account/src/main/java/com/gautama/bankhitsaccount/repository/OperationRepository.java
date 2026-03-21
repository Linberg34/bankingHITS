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
import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<Operation, UUID> {

    List<Operation> findByAccountNumber(String accountNumber);

    Page<Operation> findByAccountNumberOrderByCreatedAtDesc(String accountNumber, Pageable pageable);

    List<Operation> findByAccountNumberAndCreatedAtBetween(String accountNumber, LocalDateTime start, LocalDateTime end);

}
