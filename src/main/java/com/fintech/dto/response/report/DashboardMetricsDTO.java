package com.fintech.dto.response.report;

import java.math.BigDecimal;

public record DashboardMetricsDTO(
    // Métricas generales
    Long totalUsers,
    Long activeUsers,
    Long totalAccounts,
    Long activeAccounts,
    BigDecimal totalBalance,

    // Métricas de transacciones
    Long totalTransactions,
    Long todayTransactions,
    BigDecimal todayVolume,
    BigDecimal averageTransactionAmount,

    // Tendencias (comparación con período anterior)
    Double userGrowthRate,
    Double transactionGrowthRate,
    Double volumeGrowthRate
) {}
