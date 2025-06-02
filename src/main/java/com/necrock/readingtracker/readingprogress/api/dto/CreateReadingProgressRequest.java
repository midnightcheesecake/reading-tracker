package com.necrock.readingtracker.readingprogress.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class CreateReadingProgressRequest {

    @NotNull(message = "Reading item ID is required")
    private final Long readingItemId;

    @PositiveOrZero(message = "Last read chapter number can not be negative")
    private final Integer lastReadChapter;

    private CreateReadingProgressRequest(Long readingItemId, Integer lastReadChapter) {
        this.readingItemId = readingItemId;
        this.lastReadChapter = lastReadChapter;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getReadingItemId() {
        return readingItemId;
    }

    public Integer getLastReadChapter() {
        return lastReadChapter;
    }

    public static class Builder {
        private Long readingItemId;
        private Integer lastReadChapter;

        private Builder() {}

        public Builder readingItemId(Long id) {
            this.readingItemId = id;
            return this;
        }

        public Builder lastReadChapter(Integer chapter) {
            this.lastReadChapter = chapter;
            return this;
        }

        public CreateReadingProgressRequest build() {
            return new CreateReadingProgressRequest(readingItemId, lastReadChapter);
        }
    }
}
