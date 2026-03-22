package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.settings.SettingsDto;
import com.iisovaii.client_bff.security.CurrentUser;
import com.iisovaii.client_bff.service.SettingsService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bff/client/settings")
@RequiredArgsConstructor
@Tag(name = "Settings", description = "Настройки клиентского приложения (тема, скрытые счета)")
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    @Operation(
            summary = "Получить настройки клиента",
            description = "Возвращает тему (светлая/тёмная) и список скрытых счетов для текущего пользователя."
    )
    public ResponseEntity<SettingsDto> getSettings(
            @CurrentUser UUID userId) {
        return ResponseEntity.ok(settingsService.getSettings(userId));
    }

    @PutMapping
    @Operation(
            summary = "Обновить настройки клиента",
            description = "Сохраняет тему и список скрытых счетов для текущего пользователя."
    )
    public ResponseEntity<SettingsDto> updateSettings(
            @CurrentUser UUID userId,
            @RequestBody @Valid SettingsDto request) {
        return ResponseEntity.ok(settingsService.updateSettings(userId, request));
    }
}
