package com.necrock.readingtracker.readingprogress.api;

import com.necrock.readingtracker.readingitem.service.model.ReadingItem;
import com.necrock.readingtracker.readingprogress.api.dto.CreateReadingProgressRequest;
import com.necrock.readingtracker.readingprogress.api.dto.ReadingProgressDetailsDto;
import com.necrock.readingtracker.readingprogress.api.dto.ReadingProgressItemDto;
import com.necrock.readingtracker.readingprogress.api.dto.UpdateReadingProgressRequest;
import com.necrock.readingtracker.readingprogress.service.model.ReadingProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadingProgressMapper {

    ReadingProgressDetailsDto toDetailsDto(ReadingProgress progress);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "readingItem", source = "readingItemId")
    ReadingProgress toDomainModel(CreateReadingProgressRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "readingItem", ignore = true)
    ReadingProgress toDomainModel(UpdateReadingProgressRequest dto);

    ReadingProgressItemDto toItemDto(ReadingItem item);

    default ReadingItem.Builder toReadingItem(Long id) {
        return ReadingItem.builder().id(id);
    }
}
