package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.settings.SettingsDto;
import com.iisovaii.client_bff.security.CurrentUser;
import com.iisovaii.client_bff.service.SettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bff/client/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public ResponseEntity<SettingsDto> getSettings(
            @CurrentUser UUID userId) {
        return ResponseEntity.ok(settingsService.getSettings(userId));
    }

    @PutMapping
    public ResponseEntity<SettingsDto> updateSettings(
            @CurrentUser UUID userId,
            @RequestBody @Valid SettingsDto request) {
        return ResponseEntity.ok(settingsService.updateSettings(userId, request));
    }
}
