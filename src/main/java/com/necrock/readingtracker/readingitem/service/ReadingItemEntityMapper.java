package com.necrock.readingtracker.readingitem.service;

import com.necrock.readingtracker.readingitem.persistence.ReadingItemEntity;
import com.necrock.readingtracker.readingitem.service.model.ReadingItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReadingItemEntityMapper {

    ReadingItemEntity toEntity(ReadingItem readingItem);

    ReadingItem toDomainModel(ReadingItemEntity readingItemEntity);
}
