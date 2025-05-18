package com.necrock.readingtracker.user.api.self.dto;

import jakarta.validation.constraints.NotNull;

public record UpdatePasswordRequest(@NotNull String password) {}
