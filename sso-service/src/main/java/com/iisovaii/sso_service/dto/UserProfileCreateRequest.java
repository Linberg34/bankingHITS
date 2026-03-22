package com.iisovaii.sso_service.dto;

import java.util.UUID;

public record UserProfileCreateRequest(
        UUID id,
        String name,
        String email
) {
}
