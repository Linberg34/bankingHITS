package com.iisovaii.client_bff.repository;

import com.iisovaii.client_bff.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FcmTokenRepository extends JpaRepository<FcmToken, UUID> {

    List<FcmToken> findByUserId(UUID userId);

    Optional<FcmToken> findByToken(String token);

    void deleteByToken(String token);
}
