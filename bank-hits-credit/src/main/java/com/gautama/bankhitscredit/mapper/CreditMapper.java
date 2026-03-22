package com.gautama.bankhitscredit.mapper;

import com.gautama.bankhitscredit.dto.CreditPaymentResponse;
import com.gautama.bankhitscredit.dto.CreditResponse;
import com.gautama.bankhitscredit.dto.TariffResponse;
import com.gautama.bankhitscredit.entity.Credit;
import com.gautama.bankhitscredit.entity.CreditPayment;
import com.gautama.bankhitscredit.entity.CreditTariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CreditMapper {

    @Mapping(target = "tariffName", source = "tariff.name")
    @Mapping(target = "annualRate", source = "tariff.annualRate")
    @Mapping(target = "termDays", source = "tariff.termDays")
    CreditResponse toCreditResponse(Credit credit);

    List<CreditResponse> toCreditResponseList(List<Credit> credits);

    @Mapping(target = "creditId", source = "credit.id")
    CreditPaymentResponse toPaymentResponse(CreditPayment payment);

    List<CreditPaymentResponse> toPaymentResponseList(
            List<CreditPayment> payments
    );

    TariffResponse toTariffResponse(CreditTariff tariff);

    List<TariffResponse> toTariffResponseList(List<CreditTariff> tariffs);
}