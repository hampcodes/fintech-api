package com.fintech.model;

public enum KycStatus {
    PENDING,        // KYC pendiente de verificación
    VERIFIED,       // KYC verificado exitosamente
    REJECTED,       // KYC rechazado
    REQUIRES_UPDATE // Requiere actualización de documentos
}
