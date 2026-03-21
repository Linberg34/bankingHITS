package com.gautama.bankhitsaccount.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "operations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;

    @Column(name = "operation_type", nullable = false, length = 20)
    private String operationType; // DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT

    @Column(length = 3)
    private String currency;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(precision = 19, scale = 2)
    private BigDecimal balanceBefore;

    @Column(precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(length = 50)
    private String status; // SUCCESS, FAILED, PENDING

    @Column(length = 500)
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
