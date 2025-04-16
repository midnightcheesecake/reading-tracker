package com.necrock.readingtracker.controller;

import com.google.common.collect.ImmutableList;
import com.necrock.readingtracker.dto.CreateReadingItemDto;
import com.necrock.readingtracker.dto.ReadingItemDetailsDto;
import com.necrock.readingtracker.mapper.ReadingItemMapper;
import com.necrock.readingtracker.service.ReadingItemService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("api/items")
public class ReadingItemController {

    private final ReadingItemService service;
    private final ReadingItemMapper mapper;

    public ReadingItemController(ReadingItemService service, ReadingItemMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ReadingItemDetailsDto addItem(@RequestBody @Valid CreateReadingItemDto item) {
        return mapper.toDetailsDto(
                service.addReadingItem(
                        mapper.toEntity(item)));
    }

    @GetMapping
    public ImmutableList<ReadingItemDetailsDto> getAllItems() {
        return service.getAllReadingItems().stream()
                .map(mapper::toDetailsDto)
                .collect(toImmutableList());
    }
}
