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
import com.necrock.readingtracker.testsupport.readingProgress.TestReadingProgressEntity;
import com.necrock.readingtracker.user.persistence.UserEntity;
import com.necrock.readingtracker.user.persistence.UserRepository;
import com.necrock.readingtracker.user.service.model.User;
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

    @Test
    void addReadingProgress_savesReadingProgress() {
        ReadingProgress toSaveProgress = ReadingProgress.builder()
                .user(User.builder()
                        .id(69L)
                        .username("user")
                        .email("user@email.com"))
                .readingItem(ReadingItem.builder()
                        .id(1337L)
                        .title("Reading Item"))
                .lastReadChapter(10)
                .build();

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(UserEntity.builder().build()));
        when(readingItemRepository.findById(any(Long.class))).thenReturn(Optional.of(ReadingItemEntity.builder().build()));

        service.addReadingProgress(toSaveProgress);

        var captor = ArgumentCaptor.forClass(ReadingProgressEntity.class);
        verify(repository).save(captor.capture());
        ReadingProgressEntity savedEntity = captor.getValue();
        assertThat(savedEntity.getId()).isNull();
        assertThat(savedEntity.getUser().getId()).isEqualTo(toSaveProgress.getUser().getId());
        assertThat(savedEntity.getReadingItem().getId()).isEqualTo(toSaveProgress.getReadingItem().getId());
        assertThat(savedEntity.getLastReadChapter()).isEqualTo(toSaveProgress.getLastReadChapter());
    }

    @Test
    void addReadingProgress_returnsSavedReadingProgress() {
        var userId = 69L;
        var readingItemId = 1337L;
        ReadingProgress toSaveProgress =
                testReadingProgressBuilder().userId(userId).readingItemId(readingItemId).build();
        ReadingProgressEntity savedEntity =
                testReadingProgressEntityBuilder()
                        .id(42L).userId(userId).readingItemId(readingItemId).build();

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(UserEntity.builder().build()));
        when(readingItemRepository.findById(any(Long.class))).thenReturn(Optional.of(ReadingItemEntity.builder().build()));

        when(repository.save(any(ReadingProgressEntity.class)))
                .thenReturn(savedEntity);

        var result = service.addReadingProgress(toSaveProgress);

        assertReadingProgressMatchesEntity(result, savedEntity);
    }

    @Test
    void addReadingProgress_withUnknownUser() {
        var userId = 69L;
        var readingItemId = 1337L;

        when(readingItemRepository.findById(any(Long.class))).thenReturn(Optional.of(ReadingItemEntity.builder().build()));

        assertThatThrownBy(() -> service.addReadingProgress(
                testReadingProgressBuilder().userId(userId).readingItemId(readingItemId).build()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with id " + userId);
    }

    @Test
    void addReadingProgress_withUnknownReadingItem() {
        var userId = 69L;
        var readingItemId = 1337L;

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(UserEntity.builder().build()));

        assertThatThrownBy(() -> service.addReadingProgress(
                testReadingProgressBuilder().userId(userId).readingItemId(readingItemId).build()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No reading item with id " + readingItemId);
    }

    @Test
    void addReadingProgress_withExistingUserAndReadingItemCombination_throwsAlreadyExistsException() {
        var userId = 69L;
        var readingItemId = 1337L;

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(UserEntity.builder().build()));
        when(readingItemRepository.findById(any(Long.class))).thenReturn(Optional.of(ReadingItemEntity.builder().build()));

        when(repository.findByUserIdAndReadingItemId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.of(testReadingProgressEntityBuilder().build()));

        assertThatThrownBy(() -> service.addReadingProgress(
                testReadingProgressBuilder().userId(userId).readingItemId(readingItemId).build()))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage(
                        "Reading progress for user " + userId + " and reading item " + readingItemId + " already " +
                                "exists");
    }

    @Test
    void addReadingProgress_withExistingUserAndReadingItemCombination_duringSave_throwsAlreadyExistsException() {
        var userId = 69L;
        var readingItemId = 1337L;

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(UserEntity.builder().build()));
        when(readingItemRepository.findById(any(Long.class))).thenReturn(Optional.of(ReadingItemEntity.builder().build()));
        when(repository.findByUserIdAndReadingItemId(any(Long.class), any(Long.class))).thenReturn(Optional.empty());

        when(repository.save(any(ReadingProgressEntity.class)))
                .thenThrow(new DataIntegrityViolationException(ReadingProgressEntity.UNIQUE_USER_READING_ITEM));

        assertThatThrownBy(() -> service.addReadingProgress(
                testReadingProgressBuilder().userId(userId).readingItemId(readingItemId).build()))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage(
                        "Reading progress for user " + userId + " and reading item " + readingItemId + " already " +
                                "exists");
    }

    @Test
    void getReadingProgress_withId_findsReadingProgressById() {
        ReadingProgressEntity entity =
                testReadingProgressEntityBuilder().id(1L).userId(69L).readingItemId(666L).build();

        when(repository.findById(any(Long.class)))
                .thenReturn(Optional.of(entity));

        service.getReadingProgress(1L);

        var captor = ArgumentCaptor.forClass(Long.class);
        verify(repository).findById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(1L);
    }

    @Test
    void getReadingProgress_withId_returnsReadingProgress() {
        ReadingProgressEntity entity =
                testReadingProgressEntityBuilder().id(1L).userId(69L).readingItemId(666L).build();

        when(repository.findById(any(Long.class)))
                .thenReturn(Optional.of(entity));

        var result = service.getReadingProgress(1L);

        assertReadingProgressMatchesEntity(result, entity);
    }

    @Test
    void getReadingProgress_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getReadingProgress(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No reading progress with id " + id);
    }

    @Test
    void getReadingProgress_withUserAndReadingItem_findsReadingProgressByUserAndReadingItem() {
        var user = User.builder().id(69L).build();
        var readingItem = ReadingItem.builder().id(666L).build();
        ReadingProgressEntity entity =
                testReadingProgressEntityBuilder().id(1L).userId(69L).readingItemId(666L).build();

        when(repository.findByUserIdAndReadingItemId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.of(entity));

        service.getReadingProgress(user, readingItem);

        var userCaptor = ArgumentCaptor.forClass(Long.class);
        var readingItemCaptor = ArgumentCaptor.forClass(Long.class);
        verify(repository).findByUserIdAndReadingItemId(userCaptor.capture(), readingItemCaptor.capture());
        assertThat(userCaptor.getValue()).isEqualTo(user.getId());
        assertThat(readingItemCaptor.getValue()).isEqualTo(readingItem.getId());
    }

    @Test
    void getReadingProgress_withUserAndReadingItem_returnsReadingProgress() {
        var user = User.builder().id(69L).build();
        var readingItem = ReadingItem.builder().id(666L).build();
        ReadingProgressEntity entity =
                testReadingProgressEntityBuilder().id(1L).userId(69L).readingItemId(666L).build();

        when(repository.findByUserIdAndReadingItemId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.of(entity));

        var result = service.getReadingProgress(user, readingItem);

        assertReadingProgressMatchesEntity(result, entity);
    }

    @Test
    void getReadingProgress_withUnknownUserAndReadingItemCombination_throwsNotFoundException() {
        var user = User.builder().id(9999L).build();
        var readingItem = ReadingItem.builder().id(99999L).build();

        when(repository.findByUserIdAndReadingItemId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getReadingProgress(user, readingItem))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No reading progress for user " + user.getId() + " and reading item " + readingItem.getId());
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
        var id = 42L;
        ReadingProgressEntity originalEntity =
                testReadingProgressEntityBuilder()
                        .lastReadChapter(10)
                        .build();
        ReadingProgress updateMask =
                ReadingProgress.builder()
                        .lastReadChapter(12)
                        .build();

        when(repository.findById(any(Long.class)))
                .thenReturn(Optional.of(originalEntity));

        service.updateReadingProgress(id, updateMask);

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
        var id = 42L;
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

        when(repository.findById(any(Long.class)))
                .thenReturn(Optional.of(originalEntity));
        when(repository.save(any(ReadingProgressEntity.class)))
                .thenReturn(updatedEntity);

        var result = service.updateReadingProgress(id, updateMask);

        assertReadingProgressMatchesEntity(result, updatedEntity);
    }

    @Test
    void updateReadingProgress_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateReadingProgress(id, testReadingProgressBuilder().build()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No reading progress with id " + id);
    }

    @Test
    void deleteReadingProgress_deletesReadingProgress() {
        var id = 42L;
        ReadingProgressEntity deletedEntity = testReadingProgressEntityBuilder().build();

        when(repository.findById(any(Long.class)))
                .thenReturn(Optional.of(deletedEntity));

        service.deleteReadingProgress(id);

        var captor = ArgumentCaptor.forClass(ReadingProgressEntity.class);
        verify(repository).delete(captor.capture());
        assertThat(captor.getValue()).isEqualTo(deletedEntity);
    }

    @Test
    void deleteReadingProgress_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteReadingProgress(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No reading progress with id " + id);
    }

    private static void assertReadingProgressMatchesEntity(
            ReadingProgress readingProgress, ReadingProgressEntity entity) {
        assertThat(readingProgress.getId()).isEqualTo(entity.getId());
        assertThat(readingProgress.getUser().getId()).isEqualTo(entity.getUser().getId());
        assertThat(readingProgress.getReadingItem().getId()).isEqualTo(entity.getReadingItem().getId());
        assertThat(readingProgress.getLastReadChapter()).isEqualTo(entity.getLastReadChapter());
    }
}
