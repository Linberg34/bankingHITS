package com.gautama.bankhitscredit.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String accountNumber;

    @Column(precision = 19, scale = 2)
    private BigDecimal balance;

    private String status;
}