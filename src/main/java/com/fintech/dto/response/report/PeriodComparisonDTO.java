package com.fintech.dto.response.report;

import java.math.BigDecimal;

public record PeriodComparisonDTO(
    String currentPeriod,
    String previousPeriod,

    // Período actual
    Long currentTransactions,
    BigDecimal currentVolume,
    Long currentActiveUsers,

    // Período anterior
    Long previousTransactions,
    BigDecimal previousVolume,
    Long previousActiveUsers,

    // Cambios
    Double transactionChangePercent,
    Double volumeChangePercent,
    Double activeUserChangePercent
) {}
