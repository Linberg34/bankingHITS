package com.gautama.bankhitscredit.dto;

import com.gautama.bankhitscredit.entity.CreditTariff;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TariffResponse {
    private Long id;
    private String name;
    private BigDecimal annualRate;
    private LocalDateTime createdAt;

    public static TariffResponse from(CreditTariff t) {
        TariffResponse r = new TariffResponse();
        r.setId(t.getId());
        r.setName(t.getName());
        r.setAnnualRate(t.getAnnualRate());
        r.setCreatedAt(t.getCreatedAt());
        return r;
    }
}