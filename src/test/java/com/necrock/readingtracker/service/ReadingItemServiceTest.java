package com.necrock.readingtracker.service;

import com.google.common.collect.ImmutableList;
import com.necrock.readingtracker.models.ReadingItem;
import com.necrock.readingtracker.repository.ReadingItemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.necrock.readingtracker.models.ReadingItemType.BOOK;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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

        var unused = service.addReadingItem(toSave);

        var captor = ArgumentCaptor.forClass(ReadingItem.class);
        verify(repository, times(1)).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(toSave);
    }

    @Test
    void addReadingItem_returnsSavedItem() {
        var newId = 42L;
        var title = "New Book";
        var type = BOOK;
        ReadingItem toSave = ReadingItem.builder().title(title).type(type).build();
        ReadingItem saved = ReadingItem.builder().id(newId).title(title).type(type).build();
        when(repository.save(any(ReadingItem.class))).thenReturn(saved);

        var result = service.addReadingItem(toSave);

        assertThat(result.getId()).isEqualTo(newId);
    }

}