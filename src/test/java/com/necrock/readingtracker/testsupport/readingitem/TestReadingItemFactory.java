package com.necrock.readingtracker.testsupport.readingitem;

import com.necrock.readingtracker.readingitem.common.ReadingItemType;
import com.necrock.readingtracker.readingitem.persistence.ReadingItemEntity;
import com.necrock.readingtracker.readingitem.persistence.ReadingItemRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.Instant;

public class TestReadingItemFactory {

    private final ReadingItemRepository repository;
    private final Clock clock;

    private TestReadingItemFactory(ReadingItemRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public ReadingItemEntity createReadingItem(String title, String author) {
        ReadingItemEntity readingItem = ReadingItemEntity.builder()
                .title(title)
                .author(author)
                .type(ReadingItemType.BOOK)
                .totalChapters(100)
                .createdAt(Instant.now(clock))
                .build();
        return repository.save(readingItem);
    }

    @TestConfiguration
    public static class Config {
        @Bean
        public TestReadingItemFactory testReadingItemFactory(
                ReadingItemRepository repository,
                Clock clock) {
            return new TestReadingItemFactory(repository, clock);
        }
    }
}
