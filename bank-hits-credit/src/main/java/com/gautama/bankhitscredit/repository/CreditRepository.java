package com.gautama.bankhitscredit.repository;

import com.gautama.bankhitscredit.entity.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditRepository extends JpaRepository<Credit, Long> {
    List<Credit> findByClientId(Long clientId);
    List<Credit> findByStatus(Credit.CreditStatus status);
}