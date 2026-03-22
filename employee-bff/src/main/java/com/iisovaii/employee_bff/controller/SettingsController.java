package com.iisovaii.employee_bff.controller;

import com.iisovaii.employee_bff.dto.settings.SettingsDto;
import com.iisovaii.employee_bff.security.CurrentUser;
import com.iisovaii.employee_bff.service.SettingsService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/bff/employee/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public ResponseEntity<SettingsDto> getSettings(
            @Parameter(hidden = true)  @CurrentUser UUID employeeId) {
        return ResponseEntity.ok(
                settingsService.getSettings(employeeId)
        );
    }

    @PutMapping
    public ResponseEntity<SettingsDto> updateSettings(
            @Parameter(hidden = true)  @CurrentUser UUID employeeId,
            @RequestBody @Valid SettingsDto request) {
        return ResponseEntity.ok(
                settingsService.updateSettings(employeeId, request)
        );
    }
}