package com.fintech.dto.response.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BalanceEvolutionDTO(
    LocalDate date,
    BigDecimal totalBalance,
    Long activeAccounts,
    BigDecimal averageBalance
) {}
