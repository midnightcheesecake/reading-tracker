package com.necrock.readingtracker.readingitem.api;

import com.necrock.readingtracker.readingitem.api.dto.CreateReadingItemRequest;
import com.necrock.readingtracker.readingitem.api.dto.ReadingItemDetailsDto;
import com.necrock.readingtracker.readingitem.api.dto.UpdateReadingItemRequest;
import com.necrock.readingtracker.readingitem.service.model.ReadingItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadingItemMapper {

    ReadingItemDetailsDto toDetailsDto(ReadingItem readingItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ReadingItem toDomainModel(CreateReadingItemRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ReadingItem toDomainModel(UpdateReadingItemRequest dto);
}
