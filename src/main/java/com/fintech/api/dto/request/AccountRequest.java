package com.fintech.api.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AccountRequest(
        @NotBlank(message = "Account number is required")
        @Pattern(regexp = "^[0-9]{10,20}$", message = "Account number must be between 10 and 20 digits")
        String accountNumber,

        @NotBlank(message = "Owner name is required")
        @Size(min = 3, max = 100, message = "Owner name must be between 3 and 100 characters")
        String ownerName,

        @NotBlank(message = "Owner email is required")
        @Email(message = "Email must be valid")
        String ownerEmail,

        @NotNull(message = "Initial balance is required")
        @DecimalMin(value = "0.00", message = "Initial balance must be zero or positive")
        BigDecimal initialBalance
) {}
