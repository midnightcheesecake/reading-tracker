package com.necrock.readingtracker.readingprogress.service;

import com.google.common.collect.ImmutableList;
import com.necrock.readingtracker.exception.AlreadyExistsException;
import com.necrock.readingtracker.exception.NotFoundException;
import com.necrock.readingtracker.readingitem.service.ReadingItemService;
import com.necrock.readingtracker.readingprogress.persistence.SafeReadingProgressRepository;
import com.necrock.readingtracker.readingprogress.service.model.ReadingProgress;
import com.necrock.readingtracker.user.service.model.User;
import org.springframework.stereotype.Service;

import static com.google.common.collect.ImmutableList.toImmutableList;

@Service
public class ReadingProgressService {

    private final SafeReadingProgressRepository repository;
    private final ReadingProgressEntityMapper mapper;

    private final ReadingItemService readingItemService;

    public ReadingProgressService(
            SafeReadingProgressRepository repository,
            ReadingProgressEntityMapper mapper,
            ReadingItemService readingItemService) {
        this.repository = repository;
        this.mapper = mapper;
        this.readingItemService = readingItemService;
    }

    public ReadingProgress addReadingProgress(User user, ReadingProgress progress) {
        var readingItem = readingItemService.getReadingItem(progress.getReadingItem().getId());
        var enrichedProgress = progress.toBuilder().user(user).readingItem(readingItem).build();
        if (repository.findByUserIdAndReadingItemId(
                enrichedProgress.getUser().getId(),
                enrichedProgress.getReadingItem().getId())
                .isPresent()) {
            throw new AlreadyExistsException(
                    String.format(
                            "Reading progress for user %d and reading item %d already exists",
                            enrichedProgress.getUser().getId(),
                            enrichedProgress.getReadingItem().getId()));
        }
        if (enrichedProgress.getReadingItem().getId() == null) {
            throw new RuntimeException("Reading item ID somehow became null. Input: " + progress.getReadingItem().getId()
            + "  Result from ReadingItemService: " + readingItem.getId());
        }
        return mapper.toDomainModel(repository.save(mapper.toEntity(enrichedProgress)));
    }

    public ReadingProgress getReadingProgress(User user, Long readingItemId) {
        return repository.findByUserIdAndReadingItemId(user.getId(), readingItemId)
                .map(mapper::toDomainModel)
                .orElseThrow(() -> new NotFoundException(
                        String.format(
                                "No reading progress for user %d and reading item %d",
                                user.getId(),
                                readingItemId)));
    }

    public ImmutableList<ReadingProgress> getAllReadingProgressForUser(User user) {
        return repository.findAllByUserId(user.getId()).stream()
                .map(mapper::toDomainModel)
                .collect(toImmutableList());
    }

    public ReadingProgress updateReadingProgress(User user, Long readingItemId, ReadingProgress progress) {
        var existingProgress = getReadingProgress(user, readingItemId);

        var updatedProgressBuilder = existingProgress.toBuilder();
        if (progress.getLastReadChapter() != null) {
            updatedProgressBuilder.lastReadChapter(progress.getLastReadChapter());
        }

        return mapper.toDomainModel(repository.save(mapper.toEntity(updatedProgressBuilder.build())));
    }

    public void deleteReadingProgress(User user, Long readingItemId) {
        var progress = repository.findByUserIdAndReadingItemId(user.getId(), readingItemId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(
                                "No reading progress for user %d and reading item %d",
                                user.getId(),
                                readingItemId)));
        repository.delete(progress);
    }
}
