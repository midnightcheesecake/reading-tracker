package com.necrock.readingtracker.dto;

import com.necrock.readingtracker.models.ReadingItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class CreateReadingItemDto {

    @NotBlank(message = "Title is required")
    private final String title;

    @NotNull(message = "Type is required")
    private final ReadingItemType type;

    @NotBlank(message = "Author is required")
    private final String author;

    @NotNull(message = "Number of chapters is required")
    @PositiveOrZero(message = "Number of chapters can not be negative")
    private final Integer numberChapters;

    private CreateReadingItemDto(String title, ReadingItemType type, String author, Integer numberChapters) {
        this.title = title;
        this.type = type;
        this.author = author;
        this.numberChapters = numberChapters;
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

    public Integer getNumberChapters() {
        return numberChapters;
    }

    public static class Builder {

        private String title;

        private ReadingItemType type;

        private String author;

        private Integer numberChapters;

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

        public Builder numberChapters(Integer numberChapters) {
            this.numberChapters = numberChapters;
            return this;
        }

        public CreateReadingItemDto build() {
            return new CreateReadingItemDto(title, type, author, numberChapters);
        }
    }
}
