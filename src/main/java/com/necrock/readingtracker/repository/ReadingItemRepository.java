package com.necrock.readingtracker.repository;

import com.necrock.readingtracker.models.ReadingItem;
import org.springframework.data.repository.CrudRepository;

public interface ReadingItemRepository extends CrudRepository<ReadingItem, Long> {}
