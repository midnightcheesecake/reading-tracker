package com.necrock.readingtracker.readingitem.api;

import com.necrock.readingtracker.readingitem.api.dto.CreateReadingItemDto;
import com.necrock.readingtracker.readingitem.api.dto.ReadingItemDetailsDto;
import com.necrock.readingtracker.readingitem.api.dto.UpdateReadingItemDto;
import com.necrock.readingtracker.readingitem.persistence.ReadingItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadingItemMapper {

    ReadingItemDetailsDto toDetailsDto(ReadingItem readingItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ReadingItem toEntity(CreateReadingItemDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ReadingItem toEntity(UpdateReadingItemDto dto);
}
