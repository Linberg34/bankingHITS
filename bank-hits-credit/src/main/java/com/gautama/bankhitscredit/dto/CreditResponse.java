package com.gautama.bankhitscredit.dto;

import com.gautama.bankhitscredit.entity.Credit;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreditResponse {
    private Long id;
    private Long clientId;
    private Long accountId;
    private String tariffName;
    private BigDecimal annualRate;
    private BigDecimal principalAmount;
    private BigDecimal remainingDebt;
    private LocalDateTime issuedAt;
    private LocalDateTime closedAt;
    private Credit.CreditStatus status;

    public static CreditResponse from(Credit c) {
        CreditResponse r = new CreditResponse();
        r.setId(c.getId());
        r.setClientId(c.getClientId());
        r.setAccountId(c.getAccountId());
        r.setTariffName(c.getTariff().getName());
        r.setAnnualRate(c.getTariff().getAnnualRate());
        r.setPrincipalAmount(c.getPrincipalAmount());
        r.setRemainingDebt(c.getRemainingDebt());
        r.setIssuedAt(c.getIssuedAt());
        r.setClosedAt(c.getClosedAt());
        r.setStatus(c.getStatus());
        return r;
    }
}