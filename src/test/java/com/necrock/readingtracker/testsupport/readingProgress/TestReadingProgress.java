package com.necrock.readingtracker.testsupport.readingProgress;

import com.necrock.readingtracker.readingitem.service.model.ReadingItem;
import com.necrock.readingtracker.readingprogress.service.model.ReadingProgress;
import com.necrock.readingtracker.user.service.model.User;

import java.time.Instant;

import static com.necrock.readingtracker.readingitem.common.ReadingItemType.ARTICLE;
import static com.necrock.readingtracker.user.common.UserRole.USER;
import static com.necrock.readingtracker.user.common.UserStatus.ACTIVE;

public class TestReadingProgress {
    private TestReadingProgress() {
    }

    public static Builder testReadingProgressBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final ReadingProgress.Builder builder = ReadingProgress.builder()
                .id(666L)
                .lastReadChapter(13);
        private final User.Builder userBuilder = User.builder()
                .id(666L)
                .username("username")
                .email("email@provider.com")
                .passwordHash("#hash")
                .status(ACTIVE)
                .role(USER)
                .createdAt(Instant.parse("2020-01-01T00:00:00Z"));
        private final ReadingItem.Builder readingItemBuilder = ReadingItem.builder()
                .id(666L)
                .title("an article")
                .type(ARTICLE)
                .author("an author")
                .totalChapters(500)
                .createdAt(Instant.parse("2020-01-01T00:00:00Z"));

        private Builder() {
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

        public ReadingProgress build() {
            return builder.user(userBuilder).readingItem(readingItemBuilder).build();
        }
    }
}
