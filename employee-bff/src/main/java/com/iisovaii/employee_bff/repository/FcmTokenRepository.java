package com.iisovaii.employee_bff.repository;

import com.iisovaii.employee_bff.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FcmTokenRepository extends JpaRepository<FcmToken, UUID> {

    List<FcmToken> findByEmployeeId(UUID employeeId);

    Optional<FcmToken> findByToken(String token);

    void deleteByToken(String token);
}
