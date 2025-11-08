package com.fintech.dto.response.report;

import java.math.BigDecimal;

public record TransactionReportDTO(
    String period,              // "2025-01", "2025-01-15", "2025-W01"
    Long totalTransactions,
    Long totalDeposits,
    Long totalWithdrawals,
    BigDecimal totalDepositAmount,
    BigDecimal totalWithdrawalAmount,
    BigDecimal netCashFlow      // deposits - withdrawals
) {}
