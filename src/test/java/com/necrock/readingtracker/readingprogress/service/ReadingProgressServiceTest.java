package com.necrock.readingtracker.readingprogress.service;

import com.google.common.collect.ImmutableList;
import com.necrock.readingtracker.exception.AlreadyExistsException;
import com.necrock.readingtracker.exception.NotFoundException;
import com.necrock.readingtracker.readingitem.persistence.ReadingItemEntity;
import com.necrock.readingtracker.readingitem.persistence.ReadingItemRepository;
import com.necrock.readingtracker.readingitem.service.model.ReadingItem;
import com.necrock.readingtracker.readingprogress.persistence.ReadingProgressEntity;
import com.necrock.readingtracker.readingprogress.persistence.ReadingProgressRepository;
import com.necrock.readingtracker.readingprogress.service.model.ReadingProgress;
import com.necrock.readingtracker.user.persistence.UserEntity;
import com.necrock.readingtracker.user.persistence.UserRepository;
import com.necrock.readingtracker.user.service.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static com.necrock.readingtracker.testsupport.readingProgress.TestReadingProgress.testReadingProgressBuilder;
import static com.necrock.readingtracker.testsupport.readingProgress.TestReadingProgressEntity.testReadingProgressEntityBuilder;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ReadingProgressServiceTest {

    @Autowired
    private ReadingProgressService service;

    @MockitoBean
    private ReadingProgressRepository repository;

    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private ReadingItemRepository readingItemRepository;
    @MockitoBean
    private EntityManager entityManager;

    @Test
    void addReadingProgress_savesReadingProgress() {
        User user = User.builder()
                .id(69L)
                .username("user")
                .email("user@email.com")
                .build();
        long readingItemId = 1337L;
        ReadingItemEntity readingItemEntity = testReadingItem(readingItemId);
        ReadingProgress toSaveProgress = ReadingProgress.builder()
                .readingItem(ReadingItem.builder().id(readingItemId))
                .lastReadChapter(10)
                .build();

        when(readingItemRepository.findById(any(Long.class))).thenReturn(Optional.of(readingItemEntity));
        when(entityManager.find(eq(ReadingItemEntity.class), any(Long.class))).thenReturn(readingItemEntity);

        service.addReadingProgress(user, toSaveProgress);

        var captor = ArgumentCaptor.forClass(ReadingProgressEntity.class);
        verify(repository).save(captor.capture());
        ReadingProgressEntity savedEntity = captor.getValue();
        assertThat(savedEntity.getId()).isNull();
        assertThat(savedEntity.getUser().getId()).isEqualTo(user.getId());
        assertThat(savedEntity.getReadingItem().getId()).isEqualTo(toSaveProgress.getReadingItem().getId());
        assertThat(savedEntity.getReadingItem().getTitle()).isEqualTo(readingItemEntity.getTitle());
        assertThat(savedEntity.getLastReadChapter()).isEqualTo(toSaveProgress.getLastReadChapter());
    }

    @Test
    void addReadingProgress_returnsSavedReadingProgress() {
        var userId = 69L;
        User user = User.builder().id(userId).build();
        var readingItemId = 1337L;
        ReadingItemEntity readingItemEntity = testReadingItem(readingItemId);
        ReadingProgress toSaveProgress =
                testReadingProgressBuilder().readingItemId(readingItemId).build();
        ReadingProgressEntity savedEntity =
                testReadingProgressEntityBuilder()
                        .id(42L).userId(userId).readingItem(readingItemEntity).build();

        when(readingItemRepository.findById(any(Long.class))).thenReturn(Optional.of(readingItemEntity));
        when(entityManager.find(eq(ReadingItemEntity.class), any(Long.class))).thenReturn(readingItemEntity);

        when(repository.save(any(ReadingProgressEntity.class)))
                .thenReturn(savedEntity);

        var result = service.addReadingProgress(user, toSaveProgress);

        assertReadingProgressMatchesEntity(result, savedEntity);
    }

    @Test
    void addReadingProgress_withUnknownReadingItem() {
        var userId = 69L;
        User user = User.builder().id(userId).build();
        var readingItemId = 1337L;

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(UserEntity.builder().build()));

        assertThatThrownBy(() ->
                service.addReadingProgress(user, testReadingProgressBuilder().readingItemId(readingItemId).build()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No reading item with id " + readingItemId);
    }

    @Test
    void addReadingProgress_withExistingUserAndReadingItemCombination_throwsAlreadyExistsException() {
        var userId = 69L;
        User user = User.builder().id(userId).build();
        var readingItemId = 1337L;
        ReadingItemEntity readingItemEntity = testReadingItem(readingItemId);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(UserEntity.builder().build()));
        when(readingItemRepository.findById(any(Long.class))).thenReturn(Optional.of(readingItemEntity));

        when(repository.findByUserIdAndReadingItemId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.of(testReadingProgressEntityBuilder().build()));

        assertThatThrownBy(() ->
                service.addReadingProgress(user, testReadingProgressBuilder().readingItemId(readingItemId).build()))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage(
                        "Reading progress for user " + userId + " and reading item " + readingItemId + " already " +
                                "exists");
    }

    @Test
    void addReadingProgress_withExistingUserAndReadingItemCombination_duringSave_throwsAlreadyExistsException() {
        var userId = 69L;
        User user = User.builder().id(userId).build();
        var readingItemId = 1337L;
        ReadingItemEntity readingItemEntity = testReadingItem(readingItemId);

        when(readingItemRepository.findById(any(Long.class))).thenReturn(Optional.of(readingItemEntity));
        when(entityManager.find(eq(ReadingItemEntity.class), any(Long.class))).thenReturn(readingItemEntity);
        when(repository.findByUserIdAndReadingItemId(any(Long.class), any(Long.class))).thenReturn(Optional.empty());

        when(repository.save(any(ReadingProgressEntity.class)))
                .thenThrow(new DataIntegrityViolationException(ReadingProgressEntity.UNIQUE_USER_READING_ITEM));

        assertThatThrownBy(() ->
                service.addReadingProgress(user, testReadingProgressBuilder().readingItemId(readingItemId).build()))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage(
                        "Reading progress for user " + userId + " and reading item " + readingItemId + " already " +
                                "exists");
    }

    @Test
    void getReadingProgress_findsReadingProgressByUserIdAndReadingItemId() {
        User user = User.builder().id(69L).build();
        Long readingItemId = 666L;
        ReadingProgressEntity entity =
                testReadingProgressEntityBuilder().id(1L).userId(69L).readingItemId(readingItemId).build();

        when(repository.findByUserIdAndReadingItemId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.of(entity));

        service.getReadingProgress(user, readingItemId);

        var userCaptor = ArgumentCaptor.forClass(Long.class);
        var readingItemCaptor = ArgumentCaptor.forClass(Long.class);
        verify(repository).findByUserIdAndReadingItemId(userCaptor.capture(), readingItemCaptor.capture());
        assertThat(userCaptor.getValue()).isEqualTo(user.getId());
        assertThat(readingItemCaptor.getValue()).isEqualTo(readingItemId);
    }

    @Test
    void getReadingProgress_returnsReadingProgress() {
        User user = User.builder().id(69L).build();
        Long readingItemId = 666L;
        ReadingProgressEntity entity =
                testReadingProgressEntityBuilder().id(1L).userId(69L).readingItemId(readingItemId).build();

        when(repository.findByUserIdAndReadingItemId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.of(entity));

        var result = service.getReadingProgress(user, readingItemId);

        assertReadingProgressMatchesEntity(result, entity);
    }

    @Test
    void getReadingProgress_withUnknownUserAndReadingItemCombination_throwsNotFoundException() {
        var user = User.builder().id(9999L).build();
        Long readingItemId = 666L;

        when(repository.findByUserIdAndReadingItemId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getReadingProgress(user, readingItemId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(
                        "No reading progress for user " + user.getId() + " and reading item " + readingItemId);
    }

    @Test
    void getAllReadingProgressForUser_returnsAllReadingItemsWithUser() {
        ReadingProgressEntity progress1 =
                testReadingProgressEntityBuilder().id(1L).userId(69L).readingItemId(666L).build();
        ReadingProgressEntity progress2 =
                testReadingProgressEntityBuilder().id(1L).userId(69L).readingItemId(1337L).build();
        when(repository.findAllByUserId(any(Long.class)))
                .thenReturn(ImmutableList.of(progress1, progress2));

        var result = service.getAllReadingProgressForUser(User.builder().id(69L).build());

        assertThat(result).hasSize(2);
        assertThat(result).anySatisfy(progress -> assertReadingProgressMatchesEntity(progress, progress1));
        assertThat(result).anySatisfy(progress -> assertReadingProgressMatchesEntity(progress, progress2));
    }

    @Test
    void updateReadingProgress_appliesChanges() {
        User user = User.builder().id(69L).build();
        Long readingItemId = 666L;
        ReadingProgressEntity originalEntity =
                testReadingProgressEntityBuilder()
                        .lastReadChapter(10)
                        .build();
        ReadingProgress updateMask =
                ReadingProgress.builder()
                        .lastReadChapter(12)
                        .build();

        when(repository.findByUserIdAndReadingItemId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.of(originalEntity));
        ReadingItemEntity readingItemEntity = ReadingItemEntity.builder().build();
        when(entityManager.find(eq(ReadingItemEntity.class), any(Long.class))).thenReturn(readingItemEntity);

        service.updateReadingProgress(user, readingItemId, updateMask);

        var captor = ArgumentCaptor.forClass(ReadingProgressEntity.class);
        verify(repository).save(captor.capture());
        ReadingProgressEntity updatedEntity = captor.getValue();
        assertThat(updatedEntity.getId()).isEqualTo(originalEntity.getId());
        assertThat(updatedEntity.getUser()).isEqualTo(originalEntity.getUser());
        assertThat(updatedEntity.getReadingItem()).isEqualTo(originalEntity.getReadingItem());
        assertThat(updatedEntity.getLastReadChapter()).isEqualTo(updatedEntity.getLastReadChapter());
    }

    @Test
    void updateReadingProgress_returnsNewlySavedReadingProgress() {
        User user = User.builder().id(69L).build();
        Long readingItemId = 666L;
        ReadingProgressEntity originalEntity =
                testReadingProgressEntityBuilder()
                        .lastReadChapter(10)
                        .build();
        ReadingProgress updateMask =
                ReadingProgress.builder()
                        .lastReadChapter(12)
                        .build();
        ReadingProgressEntity updatedEntity =
                testReadingProgressEntityBuilder()
                        .lastReadChapter(12)
                        .build();

        when(repository.findByUserIdAndReadingItemId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.of(originalEntity));
        when(repository.save(any(ReadingProgressEntity.class)))
                .thenReturn(updatedEntity);
        ReadingItemEntity readingItemEntity = ReadingItemEntity.builder().build();
        when(entityManager.find(eq(ReadingItemEntity.class), any(Long.class))).thenReturn(readingItemEntity);

        var result = service.updateReadingProgress(user, readingItemId, updateMask);

        assertReadingProgressMatchesEntity(result, updatedEntity);
    }

    @Test
    void updateReadingProgress_withUnknownId_throwsNotFoundException() {
        User user = User.builder().id(69L).build();
        Long readingItemId = 666L;

        when(repository.findByUserIdAndReadingItemId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateReadingProgress(user, readingItemId, testReadingProgressBuilder().build()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(
                        "No reading progress for user " + user.getId() + " and reading item " + readingItemId);
    }

    @Test
    void deleteReadingProgress_deletesReadingProgress() {
        User user = User.builder().id(69L).build();
        Long readingItemId = 666L;
        ReadingProgressEntity deletedEntity = testReadingProgressEntityBuilder().build();

        when(repository.findByUserIdAndReadingItemId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.of(deletedEntity));
        ReadingItemEntity readingItemEntity = ReadingItemEntity.builder().build();
        when(entityManager.find(eq(ReadingItemEntity.class), any(Long.class))).thenReturn(readingItemEntity);

        service.deleteReadingProgress(user, readingItemId);

        var captor = ArgumentCaptor.forClass(ReadingProgressEntity.class);
        verify(repository).delete(captor.capture());
        assertThat(captor.getValue()).isEqualTo(deletedEntity);
    }

    @Test
    void deleteReadingProgress_withUnknownId_throwsNotFoundException() {
        User user = User.builder().id(69L).build();
        Long readingItemId = 666L;

        when(repository.findByUserIdAndReadingItemId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteReadingProgress(user, readingItemId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(
                        "No reading progress for user " + user.getId() + " and reading item " + readingItemId);
    }

    private static ReadingItemEntity testReadingItem(long readingItemId) {
        return ReadingItemEntity.builder()
                .id(readingItemId)
                .title("Reading Item")
                .author("Author")
                .build();
    }

    private static void assertReadingProgressMatchesEntity(
            ReadingProgress readingProgress, ReadingProgressEntity entity) {
        assertThat(readingProgress.getId()).isEqualTo(entity.getId());
        assertThat(readingProgress.getUser().getId()).isEqualTo(entity.getUser().getId());
        assertThat(readingProgress.getReadingItem().getId()).isEqualTo(entity.getReadingItem().getId());
        assertThat(readingProgress.getReadingItem().getTitle()).isEqualTo(entity.getReadingItem().getTitle());
        assertThat(readingProgress.getReadingItem().getAuthor()).isEqualTo(entity.getReadingItem().getAuthor());
        assertThat(readingProgress.getLastReadChapter()).isEqualTo(entity.getLastReadChapter());
    }
}
