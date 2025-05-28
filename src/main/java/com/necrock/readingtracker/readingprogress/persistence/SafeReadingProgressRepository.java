package com.necrock.readingtracker.readingprogress.persistence;

import com.google.common.collect.ImmutableMap;
import com.necrock.readingtracker.common.SafeRepository;
import com.necrock.readingtracker.exception.AlreadyExistsException;
import com.necrock.readingtracker.exception.NotFoundException;
import com.necrock.readingtracker.readingitem.persistence.ReadingItemEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class SafeReadingProgressRepository extends SafeRepository<ReadingProgressEntity, Long> {
    private final ReadingProgressRepository repository;
    private final EntityManager entityManager;

    public SafeReadingProgressRepository(ReadingProgressRepository repository, EntityManager entityManager) {
        super(repository);
        this.repository = repository;
        this.entityManager = entityManager;
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

    @Override
    protected void onSave(ReadingProgressEntity progress) {
        getManagedReference(progress.getReadingItem()).addProgress(progress);
    }

    @Override
    protected void onDelete(ReadingProgressEntity progress) {
        getManagedReference(progress.getReadingItem()).removeProgress(progress);
    }

    private ReadingItemEntity getManagedReference(ReadingItemEntity unmanaged) {
        ReadingItemEntity managed = entityManager.find(ReadingItemEntity.class, unmanaged.getId());
        if (managed == null) {
            throw new NotFoundException(String.format("No reading item entity with id %d", unmanaged.getId()));
        }
        return managed;
    }
}
