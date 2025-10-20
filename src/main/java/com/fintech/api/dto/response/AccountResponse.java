package com.fintech.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        String id,
        String accountNumber,
        String ownerName,
        String ownerEmail,
        BigDecimal balance,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
