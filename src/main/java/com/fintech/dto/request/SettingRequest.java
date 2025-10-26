package com.fintech.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SettingRequest(
        @NotBlank(message = "Setting key is required")
        String settingKey,

        @NotBlank(message = "Setting value is required")
        String settingValue,

        String description
) {}
