package com.necrock.readingtracker.readingprogress.service;

import com.necrock.readingtracker.readingprogress.persistence.ReadingProgressEntity;
import com.necrock.readingtracker.readingprogress.service.model.ReadingProgress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReadingProgressEntityMapper {

    ReadingProgressEntity toEntity(ReadingProgress readingProgress);

    ReadingProgress toDomainModel(ReadingProgressEntity readingProgressEntity);
}
