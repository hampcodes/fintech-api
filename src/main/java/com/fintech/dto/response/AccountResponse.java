package com.fintech.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        String id,
        String accountNumber,
        String customerId,
        String customerName,
        BigDecimal balance,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
