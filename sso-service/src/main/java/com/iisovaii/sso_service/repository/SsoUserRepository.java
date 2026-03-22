package com.iisovaii.sso_service.repository;

import com.iisovaii.sso_service.domain.SsoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SsoUserRepository extends JpaRepository<SsoUser, UUID> {

    Optional<SsoUser> findByUsername(String username);
}
