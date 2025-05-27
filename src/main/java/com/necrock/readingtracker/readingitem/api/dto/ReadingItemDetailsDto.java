package com.necrock.readingtracker.readingitem.api.dto;

import com.necrock.readingtracker.readingitem.common.ReadingItemType;

public class ReadingItemDetailsDto {

    private final Long id;

    private final String title;

    private final ReadingItemType type;

    private final String author;

    private final Integer totalChapters;

    private ReadingItemDetailsDto(Long id, String title, ReadingItemType type, String author, Integer totalChapters) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.author = author;
        this.totalChapters = totalChapters;
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
        private Long id;
        private String title;
        private ReadingItemType type;
        private String author;
        private Integer totalChapters;

        private Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

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

        public ReadingItemDetailsDto build() {
            return new ReadingItemDetailsDto(id, title, type, author, totalChapters);
        }
    }
}
