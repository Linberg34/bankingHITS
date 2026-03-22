package com.gautama.bankhitsaccount.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class AccountListRequest {
    private UUID userId;
    private String status;
    private BigDecimal minBalance;
    private BigDecimal maxBalance;
    private Pageable pageable;
}

