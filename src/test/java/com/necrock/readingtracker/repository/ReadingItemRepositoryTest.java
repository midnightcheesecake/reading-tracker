package com.necrock.readingtracker.repository;

import com.necrock.readingtracker.models.ReadingItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ReadingItemRepositoryTest {

    @Autowired
    private ReadingItemRepository repository;

    @Test
    void save_withReadingItem_setsIdField() {
        ReadingItem readingItem = ReadingItem.builder().build();

        var savedItem = repository.save(readingItem);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isNotNull();
    }

    @Test
    void save_withReadingItem_keepsNonIdFields() {
        var title = "title";
        var author = "author";
        var type = "book";
        var numChapters = 6;
        ReadingItem readingItem =
                ReadingItem.builder().title(title).author(author).type(type).numberChapters(numChapters).build();

        var savedItem = repository.save(readingItem);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getTitle()).isEqualTo(title);
        assertThat(savedItem.getAuthor()).isEqualTo(author);
        assertThat(savedItem.getType()).isEqualTo(type);
        assertThat(savedItem.getNumberChapters()).isEqualTo(numChapters);
    }

    @Test
    void save_increasesCount() {
        long initialCount = repository.count();

        repository.save(ReadingItem.builder().title("title").build());

        long finalCount = repository.count();
        assertThat(finalCount).isEqualTo(initialCount + 1);
    }

    @Test
    void findById_withNonexistentId_returnsEmptyOptional() {
        var notFoundItem = repository.findById(1L);

        assertThat(notFoundItem).isEmpty();
    }

    @Test
    void findById_withSavedReadingItemId_returnsReadingItem() {
        var title = "title";
        var author = "author";
        var type = "book";
        var numChapters = 6;
        ReadingItem readingItem =
                ReadingItem.builder().title(title).author(author).type(type).numberChapters(numChapters).build();

        var savedItem = repository.save(readingItem);
        var foundItem = repository.findById(savedItem.getId()).orElse(null);

        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getTitle()).isEqualTo(title);
        assertThat(foundItem.getAuthor()).isEqualTo(author);
        assertThat(foundItem.getType()).isEqualTo(type);
        assertThat(foundItem.getNumberChapters()).isEqualTo(numChapters);
    }
}