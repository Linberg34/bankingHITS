package com.iisovaii.client_bff.dto.settings;

import java.util.List;
import java.util.UUID;

public record SettingsDto(
        Theme theme,
        List<UUID> hiddenAccountIds
) {}

