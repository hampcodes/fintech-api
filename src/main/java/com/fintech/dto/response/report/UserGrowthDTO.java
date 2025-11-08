package com.fintech.dto.response.report;

public record UserGrowthDTO(
    String period,
    Long newUsers,
    Long totalUsers,
    Long activeUsers
) {}
