package com.necrock.readingtracker.mapper;

import com.necrock.readingtracker.dto.CreateReadingItemDto;
import com.necrock.readingtracker.dto.ReadingItemDetailsDto;
import com.necrock.readingtracker.dto.UpdateReadingItemDto;
import com.necrock.readingtracker.models.ReadingItem;
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
