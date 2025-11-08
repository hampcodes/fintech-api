package com.fintech.dto.response.report;

import java.math.BigDecimal;

public record TransactionByAccountDTO(
    String accountNumber,
    String customerName,
    Long transactionCount,
    BigDecimal totalAmount
) {}
