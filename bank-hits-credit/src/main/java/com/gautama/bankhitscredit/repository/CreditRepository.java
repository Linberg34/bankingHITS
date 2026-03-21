package com.gautama.bankhitscredit.repository;

import com.gautama.bankhitscredit.entity.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CreditRepository extends JpaRepository<Credit, UUID> {
    List<Credit> findByClientId(UUID clientId);
    List<Credit> findByStatus(Credit.CreditStatus status);
}