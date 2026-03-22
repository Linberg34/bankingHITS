package com.iisovaii.client_bff.dto.profile;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ClientProfileResponse(
        @JsonProperty("id") UUID userId,
        String name,
        String email,
        UserStatus status
) {}

