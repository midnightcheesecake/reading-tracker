package com.necrock.readingtracker.readingprogress.api;

import com.google.common.collect.ImmutableList;
import com.necrock.readingtracker.readingprogress.api.dto.CreateReadingProgressRequest;
import com.necrock.readingtracker.readingprogress.api.dto.ReadingProgressDetailsDto;
import com.necrock.readingtracker.readingprogress.api.dto.UpdateReadingProgressRequest;
import com.necrock.readingtracker.readingprogress.service.ReadingProgressService;
import com.necrock.readingtracker.security.service.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("api/progress")
public class ReadingProgressController {

    private final ReadingProgressService service;
    private final ReadingProgressMapper mapper;

    public ReadingProgressController(ReadingProgressService service, ReadingProgressMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{readingItemId}")
    public ReadingProgressDetailsDto getReadingProgress(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long readingItemId) {
        return mapper.toDetailsDto(service.getReadingProgress(user.getUser(), readingItemId));
    }

    @GetMapping
    public ImmutableList<ReadingProgressDetailsDto> getAllReadingProgress(
            @AuthenticationPrincipal CustomUserDetails user) {
        return service.getAllReadingProgressForUser(user.getUser()).stream()
                .map(mapper::toDetailsDto)
                .collect(toImmutableList());
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ReadingProgressDetailsDto addReadingProgress(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody CreateReadingProgressRequest request) {
        return mapper.toDetailsDto(
                service.addReadingProgress(
                        user.getUser(),
                        mapper.toDomainModel(request)));
    }

    @PatchMapping("/{readingItemId}")
    public ReadingProgressDetailsDto updateReadingProgress(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long readingItemId,
            @Valid @RequestBody UpdateReadingProgressRequest request) {
        return mapper.toDetailsDto(
                service.updateReadingProgress(
                        user.getUser(),
                        readingItemId,
                        mapper.toDomainModel(request)));
    }

    @DeleteMapping("/{readingItemId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteReadingProgress(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long readingItemId) {
        service.deleteReadingProgress(user.getUser(), readingItemId);
    }
}
