package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.security.CurrentUser;
import com.iisovaii.client_bff.service.FcmNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/bff/client/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Регистрация FCM-токенов для push-уведомлений")
public class FcmTokenController {

    private final FcmNotificationService fcmNotificationService;

    @PostMapping("/fcm-token")
    @Operation(summary = "Зарегистрировать FCM-токен устройства")
    public ResponseEntity<Void> registerToken(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @RequestBody @Valid FcmTokenRequest request
    ) {
        fcmNotificationService.saveToken(userId, request.token());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/fcm-token")
    @Operation(summary = "Удалить FCM-токен устройства (при выходе из системы)")
    public ResponseEntity<Void> unregisterToken(
            @RequestBody @Valid FcmTokenRequest request
    ) {
        fcmNotificationService.removeToken(request.token());
        return ResponseEntity.noContent().build();
    }

    public record FcmTokenRequest(@NotBlank String token) {}
}
