package com.iisovaii.client_bff.service;

import com.iisovaii.client_bff.domain.Theme;
import com.iisovaii.client_bff.domain.UserSettings;
import com.iisovaii.client_bff.dto.settings.SettingsDto;
import com.iisovaii.client_bff.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final UserSettingsRepository userSettingsRepository;

    @Transactional(readOnly = true)
    public SettingsDto getSettings(UUID userId) {
        UserSettings settings = userSettingsRepository.findById(userId)
                .orElseGet(() -> new UserSettings(userId, Theme.LIGHT, new ArrayList<>()));
        return toDto(settings);
    }

    @Transactional
    public SettingsDto updateSettings(UUID userId, SettingsDto request) {
        UserSettings settings = userSettingsRepository.findById(userId)
                .orElseGet(() -> new UserSettings(userId, Theme.LIGHT, new ArrayList<>()));

        // Обновляем только те поля, которые явно переданы в запросе
        if (request.theme() != null) {
            settings.setTheme(Theme.valueOf(request.theme().name()));
        }
        if (request.hiddenAccountIds() != null) {
            settings.setHiddenAccountIds(new ArrayList<>(request.hiddenAccountIds()));
        }

        UserSettings saved = userSettingsRepository.save(settings);
        return toDto(saved);
    }

    private SettingsDto toDto(UserSettings settings) {
        return new SettingsDto(
                com.iisovaii.client_bff.dto.settings.Theme.valueOf(settings.getTheme().name()),
                List.copyOf(settings.getHiddenAccountIds())
        );
    }
}

