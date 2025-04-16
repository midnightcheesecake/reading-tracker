package com.necrock.readingtracker.service;

import com.google.common.collect.ImmutableList;
import com.necrock.readingtracker.models.ReadingItem;
import com.necrock.readingtracker.repository.ReadingItemRepository;
import org.springframework.stereotype.Service;

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
        return repository.save(item);
    }
}
