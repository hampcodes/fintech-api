package com.fintech.dto.response;

import java.math.BigDecimal;

public record AccountStatisticsResponse(
        Long totalAccounts,
        Long activeAccounts,
        Long inactiveAccounts,
        BigDecimal totalBalance,
        BigDecimal averageBalance
) {}
