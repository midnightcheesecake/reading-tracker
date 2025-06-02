package com.necrock.readingtracker.readingitem.api.dto;

import com.necrock.readingtracker.readingitem.common.ReadingItemType;
import jakarta.validation.constraints.PositiveOrZero;

public class UpdateReadingItemRequest {

    private final String title;

    private final ReadingItemType type;

    private final String author;

    @PositiveOrZero(message = "Total number of chapters can not be negative")
    private final Integer totalChapters;

    private UpdateReadingItemRequest(String title, ReadingItemType type, String author, Integer totalChapters) {
        this.title = title;
        this.type = type;
        this.author = author;
        this.totalChapters = totalChapters;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTitle() {
        return title;
    }

    public ReadingItemType getType() {
        return type;
    }

    public String getAuthor() {
        return author;
    }

    public Integer getTotalChapters() {
        return totalChapters;
    }

    public static class Builder {
        private String title;
        private ReadingItemType type;
        private String author;
        private Integer totalChapters;

        private Builder() {}

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder type(ReadingItemType type) {
            this.type = type;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder totalChapters(Integer totalChapters) {
            this.totalChapters = totalChapters;
            return this;
        }

        public UpdateReadingItemRequest build() {
            return new UpdateReadingItemRequest(title, type, author, totalChapters);
        }
    }
}
