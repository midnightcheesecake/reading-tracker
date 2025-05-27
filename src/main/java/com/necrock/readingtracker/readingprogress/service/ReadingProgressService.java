package com.necrock.readingtracker.readingprogress.service;

import com.google.common.collect.ImmutableList;
import com.necrock.readingtracker.exception.AlreadyExistsException;
import com.necrock.readingtracker.exception.NotFoundException;
import com.necrock.readingtracker.readingitem.service.ReadingItemService;
import com.necrock.readingtracker.readingitem.service.model.ReadingItem;
import com.necrock.readingtracker.readingprogress.persistence.SafeReadingProgressRepository;
import com.necrock.readingtracker.readingprogress.service.model.ReadingProgress;
import com.necrock.readingtracker.user.service.UserService;
import com.necrock.readingtracker.user.service.model.User;
import org.springframework.stereotype.Service;

import static com.google.common.collect.ImmutableList.toImmutableList;

@Service
public class ReadingProgressService {

    private final SafeReadingProgressRepository repository;
    private final ReadingProgressEntityMapper mapper;

    private final UserService userService;
    private final ReadingItemService readingItemService;

    public ReadingProgressService(SafeReadingProgressRepository repository, ReadingProgressEntityMapper mapper, UserService userService, ReadingItemService readingItemService) {
        this.repository = repository;
        this.mapper = mapper;
        this.userService = userService;
        this.readingItemService = readingItemService;
    }

    public ReadingProgress addReadingProgress(ReadingProgress progress) {
        validateAddPreconditions(progress);
        return mapper.toDomainModel(repository.save(mapper.toEntity(progress)));

    }

    private void validateAddPreconditions(ReadingProgress progress) {
        Long userId = progress.getUser().getId();
        Long readingItemId = progress.getReadingItem().getId();
        // Check user exists
        userService.getUser(userId);
        // Check reading item exists
        readingItemService.getReadingItem(readingItemId);
        // Check if no other reading progress for user and reading item exists
        if (repository.findByUserIdAndReadingItemId(userId, readingItemId).isPresent()) {
            throw new AlreadyExistsException(
                    String.format(
                            "Reading progress for user %d and reading item %d already exists",
                            userId,
                            readingItemId));
        }
    }

    public ReadingProgress getReadingProgress(Long id) {
        return repository.findById(id)
                .map(mapper::toDomainModel)
                .orElseThrow(() -> new NotFoundException(String.format("No reading progress with id %d", id)));
    }

    public ReadingProgress getReadingProgress(User user, ReadingItem readingItem) {
        return repository.findByUserIdAndReadingItemId(user.getId(), readingItem.getId())
                .map(mapper::toDomainModel)
                .orElseThrow(() -> new NotFoundException(
                        String.format(
                                "No reading progress for user %d and reading item %d",
                                user.getId(),
                                readingItem.getId())));
    }

    public ImmutableList<ReadingProgress> getAllReadingProgressForUser(User user) {
        return repository.findAllByUserId(user.getId()).stream()
                .map(mapper::toDomainModel)
                .collect(toImmutableList());
    }

    public ReadingProgress updateReadingProgress(Long id, ReadingProgress progress) {
        var existingProgress = getReadingProgress(id);

        var updatedProgressBuilder = existingProgress.toBuilder();
        if (progress.getLastReadChapter() != null) {
            updatedProgressBuilder.lastReadChapter(progress.getLastReadChapter());
        }

        return mapper.toDomainModel(repository.save(mapper.toEntity(updatedProgressBuilder.build())));
    }

    public void deleteReadingProgress(Long id) {
        var item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No reading progress with id %d", id)));
        repository.delete(item);
    }
}
