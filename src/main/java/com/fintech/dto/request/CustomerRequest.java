package com.fintech.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record CustomerRequest(
        @NotBlank(message = "Name is required")
        String name,

        String phone,
        String dni,
        String address,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        String nationality,
        String occupation
) {}
