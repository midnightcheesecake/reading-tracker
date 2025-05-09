package com.necrock.readingtracker.readingitem.service.model;

import com.necrock.readingtracker.readingitem.common.ReadingItemType;

import java.time.Instant;

public class ReadingItem {
    private final Long id;
    private final String title;
    private final ReadingItemType type;
    private final String author;
    private final Integer numberChapters;
    private final Instant createdAt;

    public ReadingItem(Long id,
                       String title,
                       ReadingItemType type,
                       String author,
                       Integer numberChapters,
                       Instant createdAt) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.author = author;
        this.numberChapters = numberChapters;
        this.createdAt = createdAt;
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

    public Integer getNumberChapters() {
        return numberChapters;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Builder toBuilder() {
        return builder()
                .id(id)
                .title(title)
                .type(type)
                .author(author)
                .numberChapters(numberChapters)
                .createdAt(createdAt);
    }

    public static class Builder {
        private Long id;
        private String title;
        private ReadingItemType type;
        private String author;
        private Integer numberChapters;
        private Instant createdAt;

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

        public Builder numberChapters(Integer numberChapters) {
            this.numberChapters = numberChapters;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ReadingItem build() {
            return new ReadingItem(id, title, type, author, numberChapters, createdAt);
        }
    }
}
