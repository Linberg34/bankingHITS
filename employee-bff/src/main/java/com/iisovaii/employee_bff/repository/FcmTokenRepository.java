package com.iisovaii.employee_bff.repository;

import com.iisovaii.employee_bff.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FcmTokenRepository extends JpaRepository<FcmToken, UUID> {

    Optional<FcmToken> findByToken(String token);

    void deleteByToken(String token);
}
