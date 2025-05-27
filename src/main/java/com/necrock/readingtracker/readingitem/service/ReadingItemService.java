package com.necrock.readingtracker.readingitem.service;

import com.google.common.collect.ImmutableList;
import com.necrock.readingtracker.readingitem.persistence.SafeReadingItemRepository;
import com.necrock.readingtracker.exception.NotFoundException;
import com.necrock.readingtracker.readingitem.service.model.ReadingItem;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

import static com.google.common.collect.ImmutableList.toImmutableList;

@Service
public class ReadingItemService {

    private final SafeReadingItemRepository repository;
    private final ReadingItemEntityMapper mapper;
    private final Clock clock;

    public ReadingItemService(SafeReadingItemRepository repository, ReadingItemEntityMapper mapper, Clock clock) {
        this.repository = repository;
        this.mapper = mapper;
        this.clock = clock;
    }

    public ReadingItem addReadingItem(ReadingItem item) {
        var enrichedReadingItem = item.toBuilder().createdAt(Instant.now(clock)).build();
        return mapper.toDomainModel(repository.save(mapper.toEntity(enrichedReadingItem)));
    }

    public ReadingItem getReadingItem(Long id) {
        return repository.findById(id)
                .map(mapper::toDomainModel)
                .orElseThrow(() -> new NotFoundException(String.format("No reading item with id %d", id)));
    }

    public ImmutableList<ReadingItem> getAllReadingItems() {
        return repository.findAll().stream()
                .map(mapper::toDomainModel)
                .collect(toImmutableList());
    }

    public ReadingItem updateReadingItem(Long id, ReadingItem item) {
        var existingItem = getReadingItem(id);

        var updatedItemBuilder = existingItem.toBuilder();
        if (item.getTitle() != null) {
            updatedItemBuilder.title(item.getTitle());
        }
        if (item.getType() != null) {
            updatedItemBuilder.type(item.getType());
        }
        if (item.getAuthor() != null) {
            updatedItemBuilder.author(item.getAuthor());
        }
        if (item.getTotalChapters() != null) {
            updatedItemBuilder.totalChapters(item.getTotalChapters());
        }

        return mapper.toDomainModel(repository.save(mapper.toEntity(updatedItemBuilder.build())));
    }

    public void deleteReadingItem(Long id) {
        var item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No reading item with id %d", id)));
        repository.delete(item);
    }
}
