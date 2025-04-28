package com.necrock.readingtracker.readingitem.service;

import com.google.common.collect.ImmutableList;
import com.necrock.readingtracker.readingitem.persistence.ReadingItem;
import com.necrock.readingtracker.readingitem.persistence.ReadingItemRepository;
import com.necrock.readingtracker.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
public class ReadingItemService {

    private final ReadingItemRepository repository;
    private final Clock clock;

    public ReadingItemService(ReadingItemRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public ImmutableList<ReadingItem> getAllReadingItems() {
        return ImmutableList.copyOf(repository.findAll());
    }

    public ReadingItem addReadingItem(ReadingItem item) {
        var enrichedReadingItem = item.toBuilder().createdAt(Instant.now(clock)).build();
        return repository.save(enrichedReadingItem);
    }

    public ReadingItem updateReadingItem(long id, ReadingItem item) {
        var existingItem = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No reading item with id %d", id)));

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
        if (item.getNumberChapters() != null) {
            updatedItemBuilder.numberChapters(item.getNumberChapters());
        }

        return repository.save(updatedItemBuilder.build());
    }

    public void deleteReadingItem(long id) {
        var item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No reading item with id %d", id)));
        repository.delete(item);
    }

    public ReadingItem getReadingItem(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No reading item with id %d", id)));
    }
}
