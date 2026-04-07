package com.iisovaii.client_bff.dto.settings;

import java.util.List;

public record SettingsDto(
        Theme theme,
        List<String> hiddenAccountIds
) {}

