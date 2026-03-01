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

}