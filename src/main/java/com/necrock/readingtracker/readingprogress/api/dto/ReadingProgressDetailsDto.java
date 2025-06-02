package com.necrock.readingtracker.readingprogress.api.dto;

public class ReadingProgressDetailsDto {
    private final ReadingProgressItemDto readingItem;
    private final Integer lastReadChapter;

    private ReadingProgressDetailsDto(
            ReadingProgressItemDto readingItem,
            Integer lastReadChapter) {
        this.readingItem = readingItem;
        this.lastReadChapter = lastReadChapter;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ReadingProgressItemDto getReadingItem() {
        return readingItem;
    }

    public Integer getLastReadChapter() {
        return lastReadChapter;
    }

    public static class Builder {
        private ReadingProgressItemDto readingItem;
        private Integer lastReadChapter;

        private Builder() {}

        public Builder readingItem(ReadingProgressItemDto readingItem) {
            this.readingItem = readingItem;
            return this;
        }

        public Builder lastReadChapter(Integer chapter) {
            this.lastReadChapter = chapter;
            return this;
        }

        public ReadingProgressDetailsDto build() {
            return new ReadingProgressDetailsDto(readingItem, lastReadChapter);
        }
    }
}
