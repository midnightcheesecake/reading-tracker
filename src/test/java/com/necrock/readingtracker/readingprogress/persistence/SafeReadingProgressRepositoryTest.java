package com.necrock.readingtracker.readingprogress.persistence;

import com.necrock.readingtracker.exception.AlreadyExistsException;
import com.necrock.readingtracker.exception.DatabaseException;
import com.necrock.readingtracker.readingitem.common.ReadingItemType;
import com.necrock.readingtracker.readingitem.persistence.ReadingItemEntity;
import com.necrock.readingtracker.readingitem.persistence.ReadingItemRepository;
import com.necrock.readingtracker.user.common.UserRole;
import com.necrock.readingtracker.user.persistence.UserEntity;
import com.necrock.readingtracker.user.persistence.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Transactional
@DataJpaTest
@Import(SafeReadingProgressRepository.class)
class SafeReadingProgressRepositoryTest {

    @Autowired
    private SafeReadingProgressRepository repository;

    @Autowired
    private ReadingProgressRepository unsafeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReadingItemRepository readingItemRepository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        unsafeRepository.deleteAll();
        userRepository.deleteAll();
        readingItemRepository.deleteAll();
    }

    @Test
    void save_withNewProgress_setsIdField() {
        ReadingProgressEntity readingProgress = ReadingProgressEntity.builder()
                .user(createUser())
                .readingItem(createReadingItem())
                .lastReadChapter(10)
                .build();

        var savedProgress = repository.save(readingProgress);

        assertThat(savedProgress).isNotNull();
        assertThat(savedProgress.getId()).isNotNull();
    }

    @Test
    void save_withNewProgress_keepsNonIdFields() {
        ReadingProgressEntity readingProgress = ReadingProgressEntity.builder()
                .user(createUser())
                .readingItem(createReadingItem())
                .lastReadChapter(10)
                .build();

        var savedProgress = repository.save(readingProgress);

        assertThat(savedProgress).isNotNull();
        assertThat(savedProgress.getUser()).isEqualTo(readingProgress.getUser());
        assertThat(savedProgress.getReadingItem()).isEqualTo(readingProgress.getReadingItem());
        assertThat(savedProgress.getLastReadChapter()).isEqualTo(readingProgress.getLastReadChapter());
    }

    @Test
    void save_withNewProgress_increasesCount() {
        long initialCount = unsafeRepository.count();

        ReadingProgressEntity readingProgress = ReadingProgressEntity.builder()
                .user(createUser())
                .readingItem(createReadingItem())
                .lastReadChapter(10)
                .build();

        repository.save(readingProgress);

        long finalCount = unsafeRepository.count();
        assertThat(finalCount).isEqualTo(initialCount + 1);
    }

    @Test
    void save_withNewProgress_addsProgressToReadingItem() {
        ReadingProgressEntity readingProgress = ReadingProgressEntity.builder()
                .user(createUser())
                .readingItem(createReadingItem())
                .lastReadChapter(10)
                .build();

        var savedProgress = repository.save(readingProgress);

        assertThat(savedProgress).isNotNull();
        assertThat(savedProgress.getId()).isNotNull();
    }

    @Test
    void save_withNewProgress_withExistingForeignKey_fails() {
        UserEntity user = createUser();
        ReadingItemEntity readingItem = createReadingItem();
        ReadingProgressEntity readingProgress1 = ReadingProgressEntity.builder()
                .user(user)
                .readingItem(readingItem)
                .lastReadChapter(12)
                .build();
        ReadingProgressEntity readingProgress2 = ReadingProgressEntity.builder()
                .user(user)
                .readingItem(readingItem)
                .lastReadChapter(10)
                .build();

        repository.save(readingProgress1);

        assertThatThrownBy(() -> repository.save(readingProgress2))
                .isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    void save_withNewProgress_withUnknownUser_fails() {
        UserEntity unsavedUser = UserEntity.builder()
                .username("user")
                .email("email@provider.com")
                .passwordHash("#hash")
                .role(UserRole.USER)
                .build();
        ReadingProgressEntity readingProgress = ReadingProgressEntity.builder()
                .user(unsavedUser)
                .readingItem(createReadingItem())
                .lastReadChapter(10)
                .build();

        assertThatThrownBy(() -> repository.save(readingProgress))
                .isInstanceOf(DatabaseException.class);
    }

    @Test
    void save_withNewProgress_withUnknownReadingItem_fails() {
        ReadingItemEntity unsavedReadingItem = ReadingItemEntity.builder()
                .title("title")
                .author("author")
                .type(ReadingItemType.BOOK)
                .totalChapters(20)
                .build();
        ReadingProgressEntity readingProgress = ReadingProgressEntity.builder()
                .user(createUser())
                .readingItem(unsavedReadingItem)
                .lastReadChapter(10)
                .build();

        assertThatThrownBy(() -> repository.save(readingProgress))
                .isInstanceOf(DatabaseException.class);
    }

    @Test
    void findById_withSavedId_returnsProgress() {
        ReadingProgressEntity progress = ReadingProgressEntity.builder()
                .user(createUser())
                .readingItem(createReadingItem())
                .lastReadChapter(10)
                .build();

        var savedProgress = repository.save(progress);

        var foundOptionalProgress = repository.findById(savedProgress.getId());

        assertThat(foundOptionalProgress).isNotEmpty();
        assertThat(foundOptionalProgress).hasValueSatisfying(foundProgress -> {
            assertThat(foundProgress.getUser()).isEqualTo(progress.getUser());
            assertThat(foundProgress.getReadingItem()).isEqualTo(progress.getReadingItem());
            assertThat(foundProgress.getLastReadChapter()).isEqualTo(progress.getLastReadChapter());
        });
    }

    @Test
    void findById_withNonexistentId_returnsEmptyOptional() {
        var notFoundProgress = repository.findById(1L);

        assertThat(notFoundProgress).isEmpty();
    }

    @Test
    void findByUserIdAndReadingItemId_withSavedForeignKey_returnsProgress() {
        ReadingProgressEntity progress = ReadingProgressEntity.builder()
                .user(createUser())
                .readingItem(createReadingItem())
                .lastReadChapter(10)
                .build();

        repository.save(progress);

        var foundOptionalProgress = repository.findByUserIdAndReadingItemId(
                progress.getUser().getId(),
                progress.getReadingItem().getId());

        assertThat(foundOptionalProgress).isNotEmpty();
        assertThat(foundOptionalProgress).hasValueSatisfying(foundProgress -> {
            assertThat(foundProgress.getUser()).isEqualTo(progress.getUser());
            assertThat(foundProgress.getReadingItem()).isEqualTo(progress.getReadingItem());
            assertThat(foundProgress.getLastReadChapter()).isEqualTo(progress.getLastReadChapter());
        });
    }

    @Test
    void findByUserIdAndReadingItemId_withNonexistentForeignKey_returnsEmptyOptional() {
        var notFoundProgress = repository.findByUserIdAndReadingItemId(1L, 1L);

        assertThat(notFoundProgress).isEmpty();
    }

    @Test
    void findAllByUser_returnsAllProgressForUser() {
        var user = createUser();
        ReadingProgressEntity progress1 = ReadingProgressEntity.builder()
                .user(user)
                .readingItem(createReadingItem())
                .lastReadChapter(10)
                .build();
        ReadingProgressEntity progress2 = ReadingProgressEntity.builder()
                .user(user)
                .readingItem(createReadingItem())
                .lastReadChapter(12)
                .build();

        repository.save(progress1);
        repository.save(progress2);

        var foundProgressList = repository.findAllByUserId(user.getId());

        assertThat(foundProgressList).hasSize(2);
        assertThat(foundProgressList).anySatisfy(foundProgress -> {
            assertThat(foundProgress.getUser()).isEqualTo(progress1.getUser());
            assertThat(foundProgress.getReadingItem()).isEqualTo(progress1.getReadingItem());
            assertThat(foundProgress.getLastReadChapter()).isEqualTo(progress1.getLastReadChapter());
        });
        assertThat(foundProgressList).anySatisfy(foundProgress -> {
            assertThat(foundProgress.getUser()).isEqualTo(progress2.getUser());
            assertThat(foundProgress.getReadingItem()).isEqualTo(progress2.getReadingItem());
            assertThat(foundProgress.getLastReadChapter()).isEqualTo(progress2.getLastReadChapter());
        });
    }

    @Test
    void deleteReadingItem_deletesReadingProgressForReadingItem() {
        var readingItem = createReadingItem();
        ReadingProgressEntity progress = ReadingProgressEntity.builder()
                .user(createUser())
                .readingItem(readingItem)
                .lastReadChapter(10)
                .build();

        repository.saveAndFlush(progress);

        readingItemRepository.delete(readingItem);
        entityManager.flush();

        assertThat(repository.findAll()).isEmpty();
    }

    private UserEntity createUser() {
        UserEntity user = UserEntity.builder()
                .username("user")
                .email("email@provider.com")
                .passwordHash("#hash")
                .role(UserRole.USER)
                .build();
        return userRepository.saveAndFlush(user);
    }

    private ReadingItemEntity createReadingItem() {
        ReadingItemEntity readingItem = ReadingItemEntity.builder()
                .title("title")
                .author("author")
                .type(ReadingItemType.BOOK)
                .totalChapters(20)
                .build();
        return readingItemRepository.saveAndFlush(readingItem);
    }
}