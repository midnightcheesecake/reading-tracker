package com.necrock.readingtracker.exception.handler;

import com.google.common.collect.ImmutableMap;
import org.springframework.lang.NonNull;

import java.util.Map;

import static java.util.Objects.requireNonNull;

public class ApiError {
    private final ErrorType type;
    private final String message;
    private final ImmutableMap<String, String> details;

    public ApiError(@NonNull ErrorType type, @NonNull String message) {
        this.type = requireNonNull(type);
        this.message = requireNonNull(message);
        this.details = ImmutableMap.of();
    }

    public ApiError(@NonNull ErrorType type, @NonNull String message, @NonNull Map<String, String> details) {
        this.type = requireNonNull(type);
        this.message = requireNonNull(message);
        this.details = ImmutableMap.copyOf(requireNonNull(details));
    }

    public ErrorType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public ImmutableMap<String, String> getDetails() {
        return details;
    }
}
