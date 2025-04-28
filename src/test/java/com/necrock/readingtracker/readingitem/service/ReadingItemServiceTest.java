package com.necrock.readingtracker.readingitem.service;

import com.google.common.collect.ImmutableList;
import com.necrock.readingtracker.configuration.TestTimeConfig;
import com.necrock.readingtracker.exception.NotFoundException;
import com.necrock.readingtracker.readingitem.persistence.ReadingItem;
import com.necrock.readingtracker.readingitem.persistence.ReadingItemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static com.necrock.readingtracker.readingitem.persistence.ReadingItemType.ARTICLE;
import static com.necrock.readingtracker.readingitem.persistence.ReadingItemType.BOOK;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import(TestTimeConfig.class)
@SpringBootTest
class ReadingItemServiceTest {

    @Autowired
    private ReadingItemService service;

    @MockitoBean
    private ReadingItemRepository repository;

    @Test
    void getAllReadingItems_returnsAllReadingItems() {
        ReadingItem item1 = ReadingItem.builder().id(1L).title("Book 1").type(BOOK).build();
        ReadingItem item2 = ReadingItem.builder().id(2L).title("Book 2").type(BOOK).build();
        when(repository.findAll()).thenReturn(ImmutableList.of(item1, item2));

        var result = service.getAllReadingItems();

        assertThat(result).containsExactly(item1, item2);
    }

    @Test
    void addReadingItem_savesReadingItem() {
        var title = "New Book";
        var author = "A. U. Thor";
        var numberChapters = 69;
        ReadingItem toSave = ReadingItem.builder()
                // input values
                .title(title).author(author).type(BOOK).numberChapters(numberChapters)
                .build();
        ReadingItem savedReadingItem = ReadingItem.builder()
                // input values
                .title(title).author(author).type(BOOK).numberChapters(numberChapters)
                // default values
                .createdAt(TestTimeConfig.NOW)
                .build();

        service.addReadingItem(toSave);

        var captor = ArgumentCaptor.forClass(ReadingItem.class);
        verify(repository, times(1)).save(captor.capture());
        assertThat(captor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(savedReadingItem);
    }

    @Test
    void addReadingItem_returnsSavedReadingItem() {
        var title = "New Book";
        var author = "A. U. Thor";
        var numberChapters = 69;
        ReadingItem toSave = ReadingItem.builder()
                .title(title).author(author).type(BOOK).numberChapters(numberChapters)
                .build();
        ReadingItem savedReadingItem = ReadingItem.builder()
                .id(42L)
                .title(title).author(author).type(BOOK).numberChapters(numberChapters)
                .createdAt(TestTimeConfig.NOW)
                .build();
        when(repository.save(any(ReadingItem.class))).thenReturn(savedReadingItem);

        var result = service.addReadingItem(toSave);

        assertThat(result).isEqualTo(savedReadingItem);
    }

    @Test
    void updateReadingItem_savesReadingItem() {
        var id = 42L;
        var oldTitle = "Old Title";
        var newTitle = "New Title";
        ReadingItem old = testReadingItemBuilder().id(id).title(oldTitle).build();
        ReadingItem updateMask = ReadingItem.builder().title(newTitle).build();
        ReadingItem updated = testReadingItemBuilder().id(id).title(newTitle).build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(old));

        service.updateReadingItem(id, updateMask);

        var captor = ArgumentCaptor.forClass(ReadingItem.class);
        verify(repository, times(1)).save(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(updated);
    }

    @Test
    void updateReadingItem_returnsNewlySavedReadingItem() {
        var id = 42L;
        var oldTitle = "Old Title";
        var newTitle = "New Title";
        ReadingItem old = testReadingItemBuilder().id(id).title(oldTitle).build();
        ReadingItem updateMask = ReadingItem.builder().title(newTitle).build();
        ReadingItem updated = testReadingItemBuilder().id(id).title(newTitle).build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(old));
        when(repository.save(any(ReadingItem.class))).thenReturn(updated);

        var result = service.updateReadingItem(id, updateMask);

        assertThat(result).isEqualTo(updated);
    }

    @Test
    void updateReadingItem_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateReadingItem(id, testReadingItemBuilder().build()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No reading item with id " + id);
    }

    @Test
    void deleteReadingItem_deletesReadingItem() {
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

        assertThatThrownBy(() -> service.deleteReadingItem(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No reading item with id " + id);
    }

    @Test
    void getReadingItem_findsReadingItemById() {
        var id = 42L;
        ReadingItem item = testReadingItemBuilder().id(id).build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(item));

        service.getReadingItem(id);

        var captor = ArgumentCaptor.forClass(Long.class);
        verify(repository, times(1)).findById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(id);
    }

    @Test
    void getReadingItem_returnsReadingItem() {
        var id = 42L;
        ReadingItem item = testReadingItemBuilder().id(id).build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(item));

        var result = service.getReadingItem(id);

        assertThat(result).isEqualTo(item);
    }

    @Test
    void getReadingItem_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getReadingItem(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No reading item with id " + id);
    }

    private static ReadingItem.Builder testReadingItemBuilder() {
        return ReadingItem.builder()
                .id(666L)
                .title("an article")
                .type(ARTICLE)
                .author("an author")
                .numberChapters(500);
    }
}