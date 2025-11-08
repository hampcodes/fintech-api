package com.fintech.dto.response.report;

import java.math.BigDecimal;

public record BalanceDistributionDTO(
    String range,               // "0-1000", "1000-5000", etc.
    Long accountCount,
    BigDecimal totalBalance
) {}
