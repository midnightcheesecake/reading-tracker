package com.necrock.readingtracker.service;

import com.google.common.collect.ImmutableList;
import com.necrock.readingtracker.models.ReadingItem;
import com.necrock.readingtracker.repository.ReadingItemRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReadingItemService {

    private final ReadingItemRepository repository;

    public ReadingItemService(ReadingItemRepository repository) {
        this.repository = repository;
    }

    public ImmutableList<ReadingItem> getAllReadingItems() {
        return ImmutableList.copyOf(repository.findAll());
    }

    public ReadingItem addReadingItem(ReadingItem item) {
        var itemWithDate = item.toBuilder().createdAt(Instant.now()).build();
        return repository.save(itemWithDate);
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
