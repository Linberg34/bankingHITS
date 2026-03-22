package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.profile.ClientProfileResponse;
import com.iisovaii.client_bff.security.CurrentUser;
import com.iisovaii.client_bff.service.ProxyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController("clientProfileController")
@RequestMapping("/bff/client")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Профиль клиента (ФИО, контакты, статус)")
public class ProfileController {

    private final ProxyService proxyService;

    @GetMapping("/profile")
    @Operation(
            summary = "Получить профиль клиента",
            description = "Возвращает профиль текущего клиента (ФИО, email, телефон, статус)."
    )
    public ResponseEntity<ClientProfileResponse> getProfile(
            @CurrentUser UUID userId) {
        return ResponseEntity.ok(proxyService.getClientProfile(userId));
    }
}
