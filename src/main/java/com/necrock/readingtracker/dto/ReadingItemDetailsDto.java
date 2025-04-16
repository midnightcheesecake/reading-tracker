package com.necrock.readingtracker.dto;

import jakarta.validation.constraints.NotBlank;

public class ReadingItemDetailsDto {

    private final Long id;

    @NotBlank(message = "Title is required")
    private final String title;

    private final String type;

    private final String author;

    private final Integer numberChapters;

    private ReadingItemDetailsDto(Long id, String title, String type, String author, Integer numberChapters) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.author = author;
        this.numberChapters = numberChapters;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
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

        private Long id;

        private String title;

        private String type;

        private String author;

        private Integer numberChapters;

        private Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

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

        public ReadingItemDetailsDto build() {
            return new ReadingItemDetailsDto(id, title, type, author, numberChapters);
        }
    }
}
