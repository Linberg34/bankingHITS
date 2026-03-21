package com.gautama.bankhitscredit.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;
    private UUID accountNumber;

    @Column(precision = 19, scale = 2)
    private BigDecimal balance;

    private String status;
}