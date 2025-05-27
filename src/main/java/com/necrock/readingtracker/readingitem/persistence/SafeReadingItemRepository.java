package com.necrock.readingtracker.readingitem.persistence;

import com.google.common.collect.ImmutableMap;
import com.necrock.readingtracker.common.SafeRepository;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class SafeReadingItemRepository extends SafeRepository<ReadingItemEntity, Long> {

    public SafeReadingItemRepository(ReadingItemRepository repository) {
        super(repository);
    }

    @Override
    protected ImmutableMap<String, Function<ReadingItemEntity, RuntimeException>> getUniqueConstraints() {
        return ImmutableMap.of();
    }
}
