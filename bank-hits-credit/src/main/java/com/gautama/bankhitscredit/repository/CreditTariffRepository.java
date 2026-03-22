package com.gautama.bankhitscredit.repository;

import com.gautama.bankhitscredit.entity.CreditTariff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CreditTariffRepository extends JpaRepository<CreditTariff, UUID> {
    boolean existsByName(String name);
    Optional<CreditTariff> findByName(String name);
}