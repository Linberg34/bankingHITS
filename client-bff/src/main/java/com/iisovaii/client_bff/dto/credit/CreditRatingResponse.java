package com.iisovaii.client_bff.dto.credit;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreditRatingResponse(
        UUID userId,
        int score,
        int overdueCount,
        int totalCredits,
        int activeCredits,
        String ratingLabel,
        LocalDateTime calculatedAt
) {}

