package com.iisovaii.employee_bff.dto.credit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditRatingResponse {
    private UUID userId;
    private int score;
    private int overdueCount;
    private int totalCredits;
    private int activeCredits;
    private String ratingLabel;
    private LocalDateTime calculatedAt;
}
