package com.fintech.dto.response;

import java.time.LocalDateTime;

public record SettingResponse(
        String id,
        String settingKey,
        String settingValue,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
