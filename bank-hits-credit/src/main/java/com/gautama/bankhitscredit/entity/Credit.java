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

    // ID клиента из сервиса пользователей
    @Column(nullable = false)
    private UUID clientId;

    @Column(nullable = false)
    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", nullable = false)
    private CreditTariff tariff;

    // Изначальная сумма кредита
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principalAmount;

    // Оставшийся долг (тело + накопленные проценты)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal remainingDebt;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    // null если не закрыт
    private LocalDateTime closedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditStatus status;

    @Column(nullable = false)
    private LocalDateTime nextPaymentAt;
}