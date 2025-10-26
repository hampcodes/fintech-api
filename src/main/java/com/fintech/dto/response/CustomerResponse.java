package com.fintech.dto.response;

import com.fintech.model.KycStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerResponse(
        String id,
        String userId,
        String name,
        String phone,
        String dni,
        String address,
        LocalDate dateOfBirth,
        String nationality,
        String occupation,
        KycStatus kycStatus,
        String kycDocuments,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
