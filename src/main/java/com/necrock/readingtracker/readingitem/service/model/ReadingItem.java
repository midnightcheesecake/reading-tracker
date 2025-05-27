package com.necrock.readingtracker.readingitem.service.model;

import com.necrock.readingtracker.readingitem.common.ReadingItemType;

import java.time.Instant;
import java.util.Objects;

public class ReadingItem {
    private final Long id;
    private final String title;
    private final ReadingItemType type;
    private final String author;
    private final Integer totalChapters;
    private final Instant createdAt;

    private ReadingItem(Long id,
                       String title,
                       ReadingItemType type,
                       String author,
                       Integer totalChapters,
                       Instant createdAt) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.author = author;
        this.totalChapters = totalChapters;
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

    public Integer getTotalChapters() {
        return totalChapters;
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
                .totalChapters(totalChapters)
                .createdAt(createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReadingItem that = (ReadingItem) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getTitle(), that.getTitle())
                && getType() == that.getType()
                && Objects.equals(getAuthor(), that.getAuthor())
                && Objects.equals(getTotalChapters(), that.getTotalChapters())
                && Objects.equals(getCreatedAt(), that.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(),
                getTitle(),
                getType(),
                getAuthor(),
                getTotalChapters(),
                getCreatedAt());
    }

    public static class Builder {
        private Long id;
        private String title;
        private ReadingItemType type;
        private String author;
        private Integer totalChapters;
        private Instant createdAt;

        public Builder() {}

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

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ReadingItem build() {
            return new ReadingItem(id, title, type, author, totalChapters, createdAt);
        }
    }
}
