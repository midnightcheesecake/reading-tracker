package com.necrock.readingtracker.readingprogress.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadingProgressRepository  extends JpaRepository<ReadingProgressEntity, Long> {

    Optional<ReadingProgressEntity> findByUserIdAndReadingItemId(Long userId, Long readingItemId);

    List<ReadingProgressEntity> findAllByUserId(Long userId);
}
