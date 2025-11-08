package com.fintech.dto.response.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TopAccountsByBalanceDTO(
    String accountNumber,
    String customerName,
    BigDecimal balance,
    LocalDateTime lastTransaction
) {}
