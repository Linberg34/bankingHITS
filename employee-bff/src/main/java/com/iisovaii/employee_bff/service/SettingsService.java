package com.iisovaii.employee_bff.service;

import com.iisovaii.employee_bff.domain.EmployeeSettings;
import com.iisovaii.employee_bff.dto.settings.SettingsDto;
import com.iisovaii.employee_bff.mapper.SettingsMapper;
import com.iisovaii.employee_bff.repository.EmployeeSettingsRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SettingsService {

    private final EmployeeSettingsRepository settingsRepository;
    private final SettingsMapper settingsMapper;

    @Transactional(readOnly = true)
    public SettingsDto getSettings(UUID employeeId) {
        EmployeeSettings settings = settingsRepository
                .findByEmployeeId(employeeId)
                .orElseGet(() -> createDefault(employeeId));

        return settingsMapper.toSettingsDto(settings);
    }

    public SettingsDto updateSettings(UUID employeeId, SettingsDto dto) {
        EmployeeSettings settings = settingsRepository
                .findByEmployeeId(employeeId)
                .orElseGet(() -> createDefault(employeeId));

        // обновляем только нужные поля через маппер не трогаем
        settings.setTheme(
                EmployeeSettings.Theme.valueOf(dto.getTheme().name())
        );
        settings.setHiddenAccountIds(
                dto.getHiddenAccountIds() != null
                        ? dto.getHiddenAccountIds()
                        : new ArrayList<>()
        );

        return settingsMapper.toSettingsDto(settingsRepository.save(settings));
    }

    private EmployeeSettings createDefault(UUID employeeId) {
        EmployeeSettings settings = new EmployeeSettings();
        settings.setEmployeeId(employeeId);
        settings.setTheme(EmployeeSettings.Theme.LIGHT);
        settings.setHiddenAccountIds(new ArrayList<>());
        return settingsRepository.save(settings);
    }
}