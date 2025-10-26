package com.fintech.dto.response;

public record DashboardStatisticsResponse(
        UserStatisticsResponse userStats,
        AccountStatisticsResponse accountStats,
        TransactionStatisticsResponse transactionStats
) {}
