package com.fintech.dto.response;

import java.math.BigDecimal;

public record TransactionStatisticsResponse(
        Long totalTransactions,
        Long totalDeposits,
        Long totalWithdrawals,
        BigDecimal totalDepositAmount,
        BigDecimal totalWithdrawalAmount,
        BigDecimal netCashFlow
) {}
