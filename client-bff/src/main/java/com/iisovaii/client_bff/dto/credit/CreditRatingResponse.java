package com.iisovaii.client_bff.dto.credit;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreditRatingResponse(
        @JsonProperty("clientId") UUID userId,
        int score,
        @JsonProperty("overduePaymentsCount") int overdueCount,
        int totalCredits,
        int activeCredits,
        int closedCredits,
        String ratingLabel,
        LocalDateTime calculatedAt
) {}

