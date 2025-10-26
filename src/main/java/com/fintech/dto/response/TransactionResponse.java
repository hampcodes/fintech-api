package com.fintech.dto.response;

import com.fintech.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String id,
        String accountNumber,
        String accountOwner,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        LocalDateTime timestamp,
        String description
) {}
