package com.iisovaii.client_bff.dto.profile;

import java.util.UUID;

public record ClientProfileResponse(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String phone,
        UserStatus status
) {}

