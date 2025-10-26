package com.fintech.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(
        // Datos de User (autenticación)
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        // Datos de Customer (información personal)
        @NotBlank(message = "Name is required")
        String name,

        String phone,
        String dni,
        String address,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        String nationality,
        String occupation
) {
}
