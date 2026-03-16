package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.profile.ClientProfileResponse;
import com.iisovaii.client_bff.security.CurrentUser;
import com.iisovaii.client_bff.service.ProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController("clientProfileController")
@RequestMapping("/bff/client")
@RequiredArgsConstructor
public class ProfileController {

    private final ProxyService proxyService;

    @GetMapping("/profile")
    public ResponseEntity<ClientProfileResponse> getProfile(
            @CurrentUser UUID userId) {
        return ResponseEntity.ok(proxyService.getClientProfile(userId));
    }
}
