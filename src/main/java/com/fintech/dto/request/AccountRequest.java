package com.fintech.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AccountRequest(
        @NotBlank(message = "Account number is required")
        @Pattern(regexp = "^[0-9]{10,20}$", message = "Account number must be between 10 and 20 digits")
        String accountNumber,

        @NotNull(message = "Initial balance is required")
        @DecimalMin(value = "0.00", message = "Initial balance must be zero or positive")
        BigDecimal initialBalance
) {}
