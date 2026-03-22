package com.gautama.bankhitscredit.entity;

import com.gautama.bankhitscredit.enums.CreditStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "credits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID clientId;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", nullable = false)
    private CreditTariff tariff;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principalAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal remainingDebt;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    private LocalDateTime closedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditStatus status;

    @Column(nullable = false)
    private LocalDateTime nextPaymentAt;
}