package com.necrock.readingtracker.readingitem.api;

import com.google.common.collect.ImmutableList;
import com.necrock.readingtracker.readingitem.api.dto.CreateReadingItemDto;
import com.necrock.readingtracker.readingitem.api.dto.ReadingItemDetailsDto;
import com.necrock.readingtracker.readingitem.api.dto.UpdateReadingItemDto;
import com.necrock.readingtracker.readingitem.service.ReadingItemService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("api/items")
public class ReadingItemController {

    private final ReadingItemService service;
    private final ReadingItemMapper mapper;

    public ReadingItemController(ReadingItemService service, ReadingItemMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ReadingItemDetailsDto getItem(@PathVariable Long id) {
        return mapper.toDetailsDto(service.getReadingItem(id));
    }

    @GetMapping
    public ImmutableList<ReadingItemDetailsDto> getAllItems() {
        return service.getAllReadingItems().stream()
                .map(mapper::toDetailsDto)
                .collect(toImmutableList());
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ReadingItemDetailsDto addItem(@Valid @RequestBody CreateReadingItemDto item) {
        return mapper.toDetailsDto(service.addReadingItem(mapper.toDomainModel(item)));
    }

    @PatchMapping("/{id}")
    public ReadingItemDetailsDto updateItem(@PathVariable Long id, @Valid @RequestBody UpdateReadingItemDto item) {
        return mapper.toDetailsDto(service.updateReadingItem(id, mapper.toDomainModel(item)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        service.deleteReadingItem(id);
    }
}
