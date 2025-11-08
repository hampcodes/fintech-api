package com.fintech.dto.response.report;

import com.fintech.model.TransactionType;
import java.math.BigDecimal;

public record TransactionByTypeDTO(
    TransactionType type,
    Long count,
    BigDecimal totalAmount,
    Double percentage
) {}
