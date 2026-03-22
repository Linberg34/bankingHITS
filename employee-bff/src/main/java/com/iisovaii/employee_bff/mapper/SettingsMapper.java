package com.iisovaii.employee_bff.mapper;

import com.iisovaii.employee_bff.domain.EmployeeSettings;
import com.iisovaii.employee_bff.dto.settings.SettingsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SettingsMapper {

    // маппим entity -> dto
    SettingsDto toSettingsDto(EmployeeSettings settings);

    // маппим dto -> entity
    // employeeId не трогаем — он устанавливается отдельно в сервисе
    @Mapping(target = "employeeId", ignore = true)
    EmployeeSettings toEntity(SettingsDto dto);
}
