package com.fintech.dto.response;

public record UserStatisticsResponse(
        Long totalUsers,
        Long activeUsers,
        Long inactiveUsers,
        Long adminUsers,
        Long regularUsers
) {}
