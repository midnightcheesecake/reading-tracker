package com.necrock.readingtracker.user.api.admin.dto;

import com.necrock.readingtracker.user.common.UserRole;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(@NotNull UserRole role) {}
