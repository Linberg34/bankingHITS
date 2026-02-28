package com.gautama.bankhitscredit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "credit_tariffs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditTariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // Годовая процентная ставка, например 12.5 = 12.5%
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal annualRate;

    @OneToMany(mappedBy = "tariff", fetch = FetchType.LAZY)
    private List<Credit> credits;
}