package com.fintech.dto.request;

import com.fintech.model.RoleType;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
        @NotNull(message = "Role is required")
        RoleType role
) {}
