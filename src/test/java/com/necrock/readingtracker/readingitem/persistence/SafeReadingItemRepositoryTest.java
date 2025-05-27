package com.necrock.readingtracker.readingitem.persistence;

import com.necrock.readingtracker.readingitem.common.ReadingItemType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DataJpaTest
@Import(SafeReadingItemRepository.class)
class SafeReadingItemRepositoryTest {

    @Autowired
    private SafeReadingItemRepository repository;

    @Autowired ReadingItemRepository unsafeRepository;

    @BeforeEach
    void setUp() {
        unsafeRepository.deleteAll();
    }

    @Test
    void save_withReadingItem_setsIdField() {
        ReadingItemEntity readingItem = ReadingItemEntity.builder().build();

        var savedItem = repository.save(readingItem);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isNotNull();
    }

    @Test
    void save_withReadingItem_keepsNonIdFields() {
        var title = "title";
        var author = "author";
        var type = ReadingItemType.BOOK;
        var numChapters = 6;
        ReadingItemEntity readingItem =
                ReadingItemEntity.builder().title(title).author(author).type(type).totalChapters(numChapters).build();

        var savedItem = repository.save(readingItem);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getTitle()).isEqualTo(title);
        assertThat(savedItem.getAuthor()).isEqualTo(author);
        assertThat(savedItem.getType()).isEqualTo(type);
        assertThat(savedItem.getTotalChapters()).isEqualTo(numChapters);
    }

    @Test
    void save_increasesCount() {
        long initialCount = unsafeRepository.count();

        repository.save(ReadingItemEntity.builder().title("title").build());

        long finalCount = unsafeRepository.count();
        assertThat(finalCount).isEqualTo(initialCount + 1);
    }

    @Test
    void findById_withNonexistentId_returnsEmptyOptional() {
        var notFoundItem = repository.findById(1L);

        assertThat(notFoundItem).isEmpty();
    }

    @SuppressWarnings("ConstantConditions") // Asserting foundItem is not null before checking fields
    @Test
    void findById_withSavedReadingItemId_returnsReadingItem() {
        var title = "title";
        var author = "author";
        var type = ReadingItemType.BOOK;
        var numChapters = 6;
        ReadingItemEntity readingItem =
                ReadingItemEntity.builder().title(title).author(author).type(type).totalChapters(numChapters).build();

        var savedItem = repository.save(readingItem);
        var foundItem = repository.findById(savedItem.getId()).orElse(null);

        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getTitle()).isEqualTo(title);
        assertThat(foundItem.getAuthor()).isEqualTo(author);
        assertThat(foundItem.getType()).isEqualTo(type);
        assertThat(foundItem.getTotalChapters()).isEqualTo(numChapters);
    }
}