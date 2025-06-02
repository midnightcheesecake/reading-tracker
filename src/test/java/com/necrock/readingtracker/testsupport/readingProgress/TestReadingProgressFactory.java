package com.necrock.readingtracker.testsupport.readingProgress;

import com.necrock.readingtracker.readingitem.persistence.ReadingItemEntity;
import com.necrock.readingtracker.readingprogress.persistence.ReadingProgressEntity;
import com.necrock.readingtracker.readingprogress.persistence.ReadingProgressRepository;
import com.necrock.readingtracker.user.persistence.UserEntity;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

public class TestReadingProgressFactory {

    private final ReadingProgressRepository repository;

    private TestReadingProgressFactory(ReadingProgressRepository repository) {
        this.repository = repository;
    }

    public ReadingProgressEntity createReadingProgress(UserEntity user, ReadingItemEntity readingItem) {
        ReadingProgressEntity readingProgress = ReadingProgressEntity.builder()
                .user(user)
                .readingItem(readingItem)
                .lastReadChapter(10)
                .build();
        return repository.save(readingProgress);
    }

    @TestConfiguration
    public static class Config {
        @Bean
        public TestReadingProgressFactory testReadingProgressFactory(
                ReadingProgressRepository repository) {
            return new TestReadingProgressFactory(repository);
        }
    }
}
