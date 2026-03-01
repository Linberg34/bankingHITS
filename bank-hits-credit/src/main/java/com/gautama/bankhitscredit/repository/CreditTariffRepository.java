package com.gautama.bankhitscredit.repository;

import com.gautama.bankhitscredit.entity.CreditTariff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditTariffRepository extends JpaRepository<CreditTariff, Long> {
    boolean existsByName(String name);
}