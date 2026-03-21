package com.iisovaii.client_bff.dto.profile;

import java.util.UUID;

public record ClientProfileResponse(
        UUID userId,
        String name,
        String email,
        UserStatus status
) {}

