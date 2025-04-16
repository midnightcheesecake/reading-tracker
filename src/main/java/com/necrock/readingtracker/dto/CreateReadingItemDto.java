package com.necrock.readingtracker.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateReadingItemDto {
    @NotBlank(message = "Title is required")
    private final String title;

    private final String type;

    private final String author;

    private final Integer numberChapters;

    private CreateReadingItemDto(String title, String type, String author, Integer numberChapters) {
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

    public String getType() {
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

        private String type;

        private String author;

        private Integer numberChapters;

        private Builder() {}

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder type(String type) {
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
