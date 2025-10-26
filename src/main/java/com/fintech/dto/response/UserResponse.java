package com.fintech.dto.response;

import com.fintech.model.RoleType;

import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String email,
        String name,
        RoleType role,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
