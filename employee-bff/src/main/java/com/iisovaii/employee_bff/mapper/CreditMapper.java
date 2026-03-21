// mapper/CreditMapper.java
package com.iisovaii.employee_bff.mapper;

import com.iisovaii.employee_bff.dto.credit.CreditDetailEmployeeResponse;
import com.iisovaii.employee_bff.dto.credit.CreditPaymentDto;
import com.iisovaii.employee_bff.dto.credit.CreditRatingResponse;
import com.iisovaii.employee_bff.dto.credit.CreditSummaryDto;
import com.iisovaii.employee_bff.dto.response.CreditDetailResponse;
import com.iisovaii.employee_bff.dto.response.CreditPaymentResponse;
import com.iisovaii.employee_bff.dto.response.CreditSummaryResponse;
import com.iisovaii.employee_bff.dto.response.TariffResponse;
import com.iisovaii.employee_bff.dto.tariff.CreateTariffResponse;
import com.iisovaii.employee_bff.dto.tariff.TariffDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreditMapper {
    @Mapping(target = "creditId", source = "id")
    @Mapping(target = "interestRate", source = "annualRate")
    @Mapping(target = "amount", source = "principalAmount")
    CreditSummaryDto toCreditSummaryDto(CreditSummaryResponse response);

    List<CreditSummaryDto> toCreditSummaryDtoList(
            List<CreditSummaryResponse> responses
    );

    @Mapping(target = "creditId", source = "id")
    @Mapping(target = "userId", source = "clientId")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "interestRate", source = "annualRate")
    @Mapping(target = "amount", source = "principalAmount")
    @Mapping(target = "ownerFullName", ignore = true)
    CreditDetailEmployeeResponse toCreditDetailEmployeeResponse(
            CreditDetailResponse response
    );

    @Mapping(target = "paymentId", source = "id")
    CreditPaymentDto toCreditPaymentDto(CreditPaymentResponse response);

    List<CreditPaymentDto> toCreditPaymentDtoList(
            List<CreditPaymentResponse> responses
    );

    CreditRatingResponse toCreditRatingResponse(
            CreditRatingResponse response
    );

    @Mapping(target = "tariffId", source = "id")
    TariffDto toTariffDto(TariffResponse response);

    List<TariffDto> toTariffDtoList(List<TariffResponse> responses);

    @Mapping(target = "tariffId", source = "id")
    CreateTariffResponse toCreateTariffResponse(TariffResponse response);
}
