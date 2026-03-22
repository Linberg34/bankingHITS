package com.gautama.bankhitscredit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditRatingResponse {
    private UUID clientId;
    private int score;
    private int overduePaymentsCount;
    private int totalCredits;
    private int activeCredits;
    private int closedCredits;
    private String ratingLabel;
    private LocalDateTime calculatedAt;
}
