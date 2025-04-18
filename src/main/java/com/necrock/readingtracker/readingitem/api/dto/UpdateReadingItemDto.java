package com.necrock.readingtracker.readingitem.api.dto;

import com.necrock.readingtracker.readingitem.persistence.ReadingItemType;
import jakarta.validation.constraints.PositiveOrZero;

public class UpdateReadingItemDto {

    private final String title;

    private final ReadingItemType type;

    private final String author;

    @PositiveOrZero(message = "Number of chapters can not be negative")
    private final Integer numberChapters;

    private UpdateReadingItemDto(String title, ReadingItemType type, String author, Integer numberChapters) {
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

        public UpdateReadingItemDto build() {
            return new UpdateReadingItemDto(title, type, author, numberChapters);
        }
    }
}
