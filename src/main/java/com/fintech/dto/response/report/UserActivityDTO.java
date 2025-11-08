package com.fintech.dto.response.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserActivityDTO(
    String customerId,
    String customerName,
    Long transactionCount,
    BigDecimal totalVolume,
    LocalDateTime lastActivity
) {}
