package com.necrock.readingtracker.readingprogress.service.model;

import com.necrock.readingtracker.readingitem.service.model.ReadingItem;
import com.necrock.readingtracker.user.service.model.User;

import java.util.Objects;

public class ReadingProgress {
    private final Long id;
    private final User user;
    private final ReadingItem readingItem;
    private final Integer lastReadChapter;

    private ReadingProgress(Long id, User user, ReadingItem readingItem, Integer lastReadChapter) {
        this.id = id;
        this.user = user;
        this.readingItem = readingItem;
        this.lastReadChapter = lastReadChapter;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public ReadingItem getReadingItem() {
        return readingItem;
    }

    public Integer getLastReadChapter() {
        return lastReadChapter;
    }

    public Builder toBuilder() {
        return builder()
                .id(id)
                .user(user)
                .readingItem(readingItem)
                .lastReadChapter(lastReadChapter);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReadingProgress that = (ReadingProgress) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getUser(), that.getUser())
                && Objects.equals(getReadingItem(), that.getReadingItem())
                && Objects.equals(getLastReadChapter(), that.getLastReadChapter());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(),
                getUser(),
                getReadingItem(),
                getLastReadChapter());
    }

    public static class Builder {
        private Long id;
        private User user;
        private ReadingItem readingItem;
        private Integer lastReadChapter;

        public Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder user(User.Builder user) {
            this.user = user.build();
            return this;
        }

        public Builder readingItem(ReadingItem readingItem) {
            this.readingItem = readingItem;
            return this;
        }

        public Builder readingItem(ReadingItem.Builder readingItem) {
            this.readingItem = readingItem.build();
            return this;
        }

        public Builder lastReadChapter(Integer chapter) {
            this.lastReadChapter = chapter;
            return this;
        }

        public ReadingProgress build() {
            return new ReadingProgress(id, user, readingItem, lastReadChapter);
        }
    }
}
