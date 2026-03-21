package com.iisovaii.sso_service.dto;

public record UserProfileCreateRequest(
        String name,
        String email
) {
}
