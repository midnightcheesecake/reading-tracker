package com.necrock.readingtracker.mapper;

import com.necrock.readingtracker.dto.CreateReadingItemDto;
import com.necrock.readingtracker.dto.ReadingItemDetailsDto;
import com.necrock.readingtracker.models.ReadingItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReadingItemMapper {

    ReadingItemDetailsDto toDetailsDto(ReadingItem readingItem);

    ReadingItem toEntity(CreateReadingItemDto dto);
}
