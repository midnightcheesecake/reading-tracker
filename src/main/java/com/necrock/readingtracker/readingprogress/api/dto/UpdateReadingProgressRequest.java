package com.necrock.readingtracker.readingprogress.api.dto;

import jakarta.validation.constraints.PositiveOrZero;

public class UpdateReadingProgressRequest {

    @PositiveOrZero(message = "Last read chapter number can not be negative")
    private final Integer lastReadChapter;

    private UpdateReadingProgressRequest(Integer lastReadChapter) {
        this.lastReadChapter = lastReadChapter;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getLastReadChapter() {
        return lastReadChapter;
    }

    public static class Builder {
        private Integer lastReadChapter;

        private Builder() {
        }

        public Builder lastChapterRead(Integer chapter) {
            this.lastReadChapter = chapter;
            return this;
        }

        public UpdateReadingProgressRequest build() {
            return new UpdateReadingProgressRequest(lastReadChapter);
        }
    }
}
