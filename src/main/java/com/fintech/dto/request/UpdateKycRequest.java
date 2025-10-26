package com.fintech.dto.request;

import com.fintech.model.KycStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateKycRequest(
        @NotNull(message = "KYC status is required")
        KycStatus kycStatus,

        String kycDocuments
) {}
