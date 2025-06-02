package com.necrock.readingtracker.readingitem.api.dto;

import com.necrock.readingtracker.readingitem.common.ReadingItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class CreateReadingItemRequest {

    @NotBlank(message = "Title is required")
    private final String title;

    @NotNull(message = "Type is required")
    private final ReadingItemType type;

    @NotBlank(message = "Author is required")
    private final String author;

    @PositiveOrZero(message = "Total number of chapters can not be negative")
    private final Integer totalChapters;

    private CreateReadingItemRequest(String title, ReadingItemType type, String author, Integer totalChapters) {
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

        public CreateReadingItemRequest build() {
            return new CreateReadingItemRequest(title, type, author, totalChapters);
        }
    }
}
