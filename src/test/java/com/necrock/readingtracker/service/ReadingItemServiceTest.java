package com.necrock.readingtracker.service;

import com.google.common.collect.ImmutableList;
import com.necrock.readingtracker.models.ReadingItem;
import com.necrock.readingtracker.repository.ReadingItemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static com.necrock.readingtracker.models.ReadingItemType.ARTICLE;
import static com.necrock.readingtracker.models.ReadingItemType.BOOK;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
class ReadingItemServiceTest {

    @Autowired
    private ReadingItemService service;

    @MockitoBean
    private ReadingItemRepository repository;

    @Test
    void getAllReadingItems_returnsAllItems() {
        ReadingItem item1 = ReadingItem.builder().id(1L).title("Book 1").type(BOOK).build();
        ReadingItem item2 = ReadingItem.builder().id(2L).title("Book 2").type(BOOK).build();
        when(repository.findAll()).thenReturn(ImmutableList.of(item1, item2));

        var result = service.getAllReadingItems();

        assertThat(result).containsExactly(item1, item2);
    }

    @Test
    void addReadingItem_savesItem() {
        ReadingItem toSave = ReadingItem.builder().title("New Book").type(BOOK).build();

        service.addReadingItem(toSave);

        var captor = ArgumentCaptor.forClass(ReadingItem.class);
        verify(repository, times(1)).save(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().ignoringFields("createdAt").isEqualTo(toSave);
    }

    @Test
    void addReadingItem_returnsSavedItem() {
        ReadingItem saved = ReadingItem.builder().id(42L).title("New Book").type(BOOK).build();
        when(repository.save(any(ReadingItem.class))).thenReturn(saved);

        var result = service.addReadingItem(testReadingItem());

        assertThat(result).isEqualTo(saved);
    }

    @Test
    void updateReadingItem_savesItem() {
        var id = 42L;
        var newTitle = "New Title";
        var unchangedType = BOOK;
        var unchangedAuthor = "The Author";
        ReadingItem old =
                ReadingItem.builder().id(id).title("Old Title").type(unchangedType).author(unchangedAuthor).build();
        ReadingItem updateMask = ReadingItem.builder().title(newTitle).build();
        ReadingItem updated =
                ReadingItem.builder().id(id).title(newTitle).type(unchangedType).author(unchangedAuthor).build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(old));

        service.updateReadingItem(id, updateMask);

        var captor = ArgumentCaptor.forClass(ReadingItem.class);
        verify(repository, times(1)).save(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(updated);
    }

    @Test
    void updateReadingItem_returnsNewlySavedItem() {
        var id = 42L;

        ReadingItem updated = ReadingItem.builder().id(id).title("New Title").type(BOOK).author("The Author").build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(testReadingItem()));
        when(repository.save(any(ReadingItem.class))).thenReturn(updated);

        var result = service.updateReadingItem(id, testReadingItem());

        assertThat(result).isEqualTo(updated);
    }

    @Test
    void updateReadingItem_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> service.updateReadingItem(id, testReadingItem()));

        assertThat(exception).hasMessage("No reading item with id " + id);
    }

    @Test
    void deleteReadingItem_deletesItem() {
        var id = 42L;
        ReadingItem deleted = ReadingItem.builder().id(id).title("New Title").type(BOOK).build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(deleted));

        service.deleteReadingItem(id);

        var captor = ArgumentCaptor.forClass(ReadingItem.class);
        verify(repository, times(1)).delete(captor.capture());
        assertThat(captor.getValue()).isEqualTo(deleted);
    }

    @Test
    void deleteReadingItem_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> service.deleteReadingItem(id));

        assertThat(exception).hasMessage("No reading item with id " + id);
    }

    @Test
    void getReadingItem_findsItemById() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(testReadingItem()));

        service.getReadingItem(id);

        var captor = ArgumentCaptor.forClass(Long.class);
        verify(repository, times(1)).findById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(id);
    }

    @Test
    void getReadingItem_returnsItem() {
        var id = 42L;

        ReadingItem item = ReadingItem.builder().id(id).title("New Title").type(BOOK).author("The Author").build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(item));

        var result = service.getReadingItem(id);

        assertThat(result).isEqualTo(item);
    }

    @Test
    void getReadingItem_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> service.getReadingItem(id));

        assertThat(exception).hasMessage("No reading item with id " + id);
    }

    private static ReadingItem testReadingItem() {
        return ReadingItem.builder()
                .id(666L)
                .title("an article")
                .type(ARTICLE)
                .author("an author")
                .numberChapters(500)
                .build();
    }
}