package com.necrock.readingtracker.testsupport.readingProgress;

import com.necrock.readingtracker.readingitem.persistence.ReadingItemEntity;
import com.necrock.readingtracker.readingprogress.persistence.ReadingProgressEntity;
import com.necrock.readingtracker.user.persistence.UserEntity;

import java.time.Instant;

import static com.necrock.readingtracker.readingitem.common.ReadingItemType.ARTICLE;
import static com.necrock.readingtracker.user.common.UserRole.USER;
import static com.necrock.readingtracker.user.common.UserStatus.ACTIVE;

public class TestReadingProgressEntity {
    private TestReadingProgressEntity() {
    }

    public static Builder testReadingProgressEntityBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final ReadingProgressEntity.Builder builder = ReadingProgressEntity.builder()
                .id(666L)
                .lastReadChapter(13);
        private final UserEntity.Builder userBuilder = UserEntity.builder()
                .id(666L)
                .username("username")
                .email("email@provider.com")
                .passwordHash("#hash")
                .status(ACTIVE)
                .role(USER)
                .createdAt(Instant.parse("2020-01-01T00:00:00Z"));
        private final ReadingItemEntity.Builder readingItemBuilder = ReadingItemEntity.builder()
                .id(666L)
                .title("an article")
                .type(ARTICLE)
                .author("an author")
                .totalChapters(500)
                .createdAt(Instant.parse("2020-01-01T00:00:00Z"));

        private Builder() {
        }

        public Builder id(Long id) {
            builder.id(id);
            return this;
        }

        public Builder userId(Long id) {
            userBuilder.id(id);
            return this;
        }

        public Builder readingItemId(Long id) {
            readingItemBuilder.id(id);
            return this;
        }

        public Builder lastReadChapter(Integer chapter) {
            builder.lastReadChapter(chapter);
            return this;
        }

        public ReadingProgressEntity build() {
            return builder.user(userBuilder).readingItem(readingItemBuilder).build();
        }
    }
}
