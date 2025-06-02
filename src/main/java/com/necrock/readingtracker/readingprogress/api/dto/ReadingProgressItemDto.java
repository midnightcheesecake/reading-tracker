package com.necrock.readingtracker.readingprogress.api.dto;

public class ReadingProgressItemDto {
    private final Long id;
    private final String title;
    private final String author;
    private final Integer totalChapters;

    private ReadingProgressItemDto(
            Long id,
            String title,
            String author,
            Integer totalChapters) {
        this.id = id;
        this.title = title;
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

    public String getAuthor() {
        return author;
    }

    public Integer getTotalChapters() {
        return totalChapters;
    }

    public static class Builder {
        private Long id;
        private String title;
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

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder totalChapters(Integer totalChapters) {
            this.totalChapters = totalChapters;
            return this;
        }

        public ReadingProgressItemDto build() {
            return new ReadingProgressItemDto(id, title, author, totalChapters);
        }
    }
}
