package com.necrock.readingtracker.readingitem.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingItemRepository extends JpaRepository<ReadingItemEntity, Long> {}
