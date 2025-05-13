package com.necrock.readingtracker.user.api.admin.dto;

import com.necrock.readingtracker.user.common.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(@NotNull UserStatus status) {}
