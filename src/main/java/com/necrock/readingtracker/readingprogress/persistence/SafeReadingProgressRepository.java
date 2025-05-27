package com.necrock.readingtracker.readingprogress.persistence;

import com.google.common.collect.ImmutableMap;
import com.necrock.readingtracker.common.SafeRepository;
import com.necrock.readingtracker.exception.AlreadyExistsException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class SafeReadingProgressRepository extends SafeRepository<ReadingProgressEntity, Long> {
    private final ReadingProgressRepository repository;

    public SafeReadingProgressRepository(ReadingProgressRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    protected ImmutableMap<String, Function<ReadingProgressEntity, RuntimeException>> getUniqueConstraints() {
        return ImmutableMap.of(
                ReadingProgressEntity.UNIQUE_USER_READING_ITEM,
                entity -> new AlreadyExistsException(
                        String.format(
                                "Reading progress for user %d and reading item %d already exists",
                                entity.getUser().getId(),
                                entity.getReadingItem().getId()))
        );
    }

    public Optional<ReadingProgressEntity> findByUserIdAndReadingItemId(Long userId, Long readingItemId) {
        return repository.findByUserIdAndReadingItemId(userId, readingItemId);
    }

    public List<ReadingProgressEntity> findAllByUserId(Long id) {
        return repository.findAllByUserId(id);
    }
}
