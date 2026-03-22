package com.iisovaii.client_bff.repository;

import com.iisovaii.client_bff.domain.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserSettingsRepository extends JpaRepository<UserSettings, UUID> {}

