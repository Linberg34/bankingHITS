package com.iisovaii.employee_bff.dto.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDto {
    private Theme theme;
    private List<UUID> hiddenAccountIds;

    public enum Theme {
        LIGHT, DARK
    }
}
