package com.necrock.readingtracker.readingitem.service;

import com.google.common.collect.ImmutableList;
import com.necrock.readingtracker.testsupport.configuration.TestTimeConfig;
import com.necrock.readingtracker.exception.NotFoundException;
import com.necrock.readingtracker.readingitem.persistence.ReadingItemEntity;
import com.necrock.readingtracker.readingitem.persistence.ReadingItemRepository;
import com.necrock.readingtracker.readingitem.service.model.ReadingItem;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.function.Consumer;

import static com.necrock.readingtracker.readingitem.common.ReadingItemType.ARTICLE;
import static com.necrock.readingtracker.readingitem.common.ReadingItemType.BOOK;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
        ReadingItemEntity item1 = ReadingItemEntity.builder().id(1L).title("Book 1").type(BOOK).build();
        ReadingItemEntity item2 = ReadingItemEntity.builder().id(2L).title("Book 2").type(BOOK).build();
        when(repository.findAll()).thenReturn(ImmutableList.of(item1, item2));

        var result = service.getAllReadingItems();

        assertThat(result).hasSize(2);
        assertThat(result).anySatisfy(item -> assertReadingItemMatchesEntity(item, item1));
        assertThat(result).anySatisfy(item -> assertReadingItemMatchesEntity(item, item2));
    }

    @Test
    void addReadingItem_savesReadingItem() {
        ReadingItem toSaveReadingItem = ReadingItem.builder()
                .title("New Book")
                .author("A. U. Thor")
                .type(BOOK)
                .totalChapters(69)
                .build();

        service.addReadingItem(toSaveReadingItem);

        var captor = ArgumentCaptor.forClass(ReadingItemEntity.class);
        verify(repository).save(captor.capture());
        ReadingItemEntity savedReadingItemEntity = captor.getValue();
        assertThat(savedReadingItemEntity.getId()).isNull();
        assertThat(savedReadingItemEntity.getTitle()).isEqualTo(toSaveReadingItem.getTitle());
        assertThat(savedReadingItemEntity.getType()).isEqualTo(toSaveReadingItem.getType());
        assertThat(savedReadingItemEntity.getAuthor()).isEqualTo(toSaveReadingItem.getAuthor());
        assertThat(savedReadingItemEntity.getTotalChapters()).isEqualTo(toSaveReadingItem.getTotalChapters());
        assertThat(savedReadingItemEntity.getCreatedAt()).isEqualTo(TestTimeConfig.NOW);
    }

    @Test
    void addReadingItem_returnsSavedReadingItem() {
        var title = "New Book";
        var author = "A. U. Thor";
        var totalChapters = 69;
        ReadingItem toSaveReadingItem = ReadingItem.builder()
                .title(title).author(author).type(BOOK).totalChapters(totalChapters)
                .build();
        ReadingItemEntity savedEntity = ReadingItemEntity.builder()
                .id(42L)
                .title(title).author(author).type(BOOK).totalChapters(totalChapters)
                .createdAt(TestTimeConfig.NOW)
                .build();
        when(repository.save(any(ReadingItemEntity.class))).thenReturn(savedEntity);

        var result = service.addReadingItem(toSaveReadingItem);

        assertReadingItemMatchesEntity(result, savedEntity);
    }

    @Test
    void updateReadingItem_appliesChanges() {
        var id = 42L;
        ReadingItemEntity originalEntity =
                testReadingItemEntity(ri -> ri
                        .id(id)
                        .title("Old Title"));
        ReadingItem updateMask =
                ReadingItem.builder()
                        .title("New Title")
                        .build();

        when(repository.findById(any(Long.class)))
                .thenReturn(Optional.of(originalEntity));

        service.updateReadingItem(id, updateMask);

        var captor = ArgumentCaptor.forClass(ReadingItemEntity.class);
        verify(repository).save(captor.capture());
        ReadingItemEntity updatedEntity = captor.getValue();
        assertThat(updatedEntity.getId()).isEqualTo(originalEntity.getId());
        assertThat(updatedEntity.getTitle()).isEqualTo(updateMask.getTitle());
        assertThat(updatedEntity.getType()).isEqualTo(originalEntity.getType());
        assertThat(updatedEntity.getAuthor()).isEqualTo(originalEntity.getAuthor());
        assertThat(updatedEntity.getTotalChapters()).isEqualTo(originalEntity.getTotalChapters());
        assertThat(updatedEntity.getCreatedAt()).isEqualTo(originalEntity.getCreatedAt());
    }

    @Test
    void updateReadingItem_returnsNewlySavedReadingItem() {
        var id = 42L;
        ReadingItemEntity originalEntity =
                testReadingItemEntity(ri -> ri
                        .id(id)
                        .title("Old Title"));
        ReadingItem updateMask =
                ReadingItem.builder()
                        .title("New Title")
                        .build();
        ReadingItemEntity updatedEntity =
                testReadingItemEntity(ri -> ri
                        .id(id)
                        .title("New Title"));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(originalEntity));
        when(repository.save(any(ReadingItemEntity.class))).thenReturn(updatedEntity);

        var result = service.updateReadingItem(id, updateMask);

        assertReadingItemMatchesEntity(result, updatedEntity);
    }

    @Test
    void updateReadingItem_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateReadingItem(id, testReadingItem()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No reading item with id " + id);
    }

    @Test
    void deleteReadingItem_deletesReadingItem() {
        var id = 42L;
        ReadingItemEntity deletedEntity = testReadingItemEntity(ri -> ri.id(id));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(deletedEntity));

        service.deleteReadingItem(id);

        var captor = ArgumentCaptor.forClass(ReadingItemEntity.class);
        verify(repository).delete(captor.capture());
        assertThat(captor.getValue()).isEqualTo(deletedEntity);
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
        ReadingItemEntity entity = testReadingItemEntity(ri -> ri.id(id));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(entity));

        service.getReadingItem(id);

        var captor = ArgumentCaptor.forClass(Long.class);
        verify(repository).findById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(id);
    }

    @Test
    void getReadingItem_returnsReadingItem() {
        var id = 42L;
        ReadingItemEntity entity = testReadingItemEntity(ri -> ri.id(id));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(entity));

        var result = service.getReadingItem(id);

        assertReadingItemMatchesEntity(result, entity);
    }

    @Test
    void getReadingItem_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getReadingItem(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No reading item with id " + id);
    }

    private static ReadingItem testReadingItem() {
        return testReadingItem(ri -> {});
    }

    private static ReadingItem testReadingItem(Consumer<ReadingItem.Builder> overrides) {
        var builder = ReadingItem.builder()
                .id(666L)
                .title("an article")
                .type(ARTICLE)
                .author("an author")
                .totalChapters(500);
        overrides.accept(builder);
        return builder.build();
    }

    private static ReadingItemEntity testReadingItemEntity(Consumer<ReadingItemEntity.Builder> overrides) {
        var builder = ReadingItemEntity.builder()
                .id(666L)
                .title("an article")
                .type(ARTICLE)
                .author("an author")
                .totalChapters(500);
        overrides.accept(builder);
        return builder.build();
    }

    private static void assertReadingItemMatchesEntity(ReadingItem readingItem, ReadingItemEntity entity) {
        assertThat(readingItem.getId()).isEqualTo(entity.getId());
        assertThat(readingItem.getTitle()).isEqualTo(entity.getTitle());
        assertThat(readingItem.getType()).isEqualTo(entity.getType());
        assertThat(readingItem.getAuthor()).isEqualTo(entity.getAuthor());
        assertThat(readingItem.getTotalChapters()).isEqualTo(entity.getTotalChapters());
        assertThat(readingItem.getCreatedAt()).isEqualTo(entity.getCreatedAt());
    }
}