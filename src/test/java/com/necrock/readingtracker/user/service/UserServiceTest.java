package com.necrock.readingtracker.user.service;

import com.necrock.readingtracker.configuration.TestTimeConfig;
import com.necrock.readingtracker.exception.AlreadyExistsException;
import com.necrock.readingtracker.exception.NotFoundException;
import com.necrock.readingtracker.user.persistence.UserEntity;
import com.necrock.readingtracker.user.persistence.UserRepository;
import com.necrock.readingtracker.user.service.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.function.Consumer;

import static com.necrock.readingtracker.user.common.UserRole.ADMIN;
import static com.necrock.readingtracker.user.common.UserRole.USER;
import static com.necrock.readingtracker.user.common.UserStatus.ACTIVE;
import static com.necrock.readingtracker.user.common.UserStatus.DELETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import(TestTimeConfig.class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService service;

    @MockitoBean
    private UserRepository repository;

    @Test
    void addUser_savesUser() {
        User toSaveUser = User.builder()
                .username("user")
                .email("email@provider.com")
                .passwordHash("#hash")
                .build();

        service.addUser(toSaveUser);

        var captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository).save(captor.capture());
        UserEntity savedUserEntity = captor.getValue();
        assertThat(savedUserEntity.getId()).isNull();
        assertThat(savedUserEntity.getUsername()).isEqualTo(toSaveUser.getUsername());
        assertThat(savedUserEntity.getEmail()).isEqualTo(toSaveUser.getEmail());
        assertThat(savedUserEntity.getPasswordHash()).isEqualTo(toSaveUser.getPasswordHash());
        assertThat(savedUserEntity.getRole()).isEqualTo(USER);
        assertThat(savedUserEntity.getStatus()).isEqualTo(ACTIVE);
        assertThat(savedUserEntity.getCreatedAt()).isEqualTo(TestTimeConfig.NOW);
    }

    @Test
    void assUser_returnsSavedUser() {
        var username = "user";
        var email = "email@provider.com";
        var passwordHash = "#hash";
        User toSaveUser = User.builder()
                .username(username).email(email).passwordHash(passwordHash)
                .build();
        UserEntity savedUserEntity = UserEntity.builder()
                .setId(42L)
                .setUsername(username)
                .setEmail(email)
                .setPasswordHash(passwordHash)
                .setStatus(ACTIVE)
                .setRole(USER)
                .setCreatedAt(TestTimeConfig.NOW)
                .build();

        when(repository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        var result = service.addUser(toSaveUser);

        assertUserMatchesEntity(result, savedUserEntity);
    }

    @Test
    void addUser_withExistingUsername_throwsAlreadyExistsException() {
        var username = "username";

        when(repository.save(any(UserEntity.class))).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.addUser(testUser(u -> u.username(username))))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage("User with username '" + username + "' already exists");
    }

    @Test
    void updateUser_appliesChanges() {
        var id = 42L;
        UserEntity originalUserEntity =
                testUserEntity(u -> u
                        .setId(id)
                        .setEmail("old.email@provider.com"));
        User updateMask =
                User.builder()
                        .email("new.email@provider.net")
                        .build();

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(originalUserEntity));

        service.updateUser(id, updateMask);

        var captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository).save(captor.capture());
        UserEntity savedUserEntity = captor.getValue();
        assertThat(savedUserEntity.getId()).isEqualTo(originalUserEntity.getId());
        assertThat(savedUserEntity.getUsername()).isEqualTo(originalUserEntity.getUsername());
        assertThat(savedUserEntity.getEmail()).isEqualTo(updateMask.getEmail());
        assertThat(savedUserEntity.getRole()).isEqualTo(originalUserEntity.getRole());
        assertThat(savedUserEntity.getStatus()).isEqualTo(originalUserEntity.getStatus());
    }

    @Test
    void updateUser_returnsNewlySavedUser() {
        var id = 42L;
        UserEntity originalUserEntity =
                testUserEntity(u -> u
                        .setId(id)
                        .setEmail("old.email@provider.com"));
        User updateMask = User.builder().email("new.email@provider.net").build();
        UserEntity updatedUserEntity =
                testUserEntity(u -> u
                        .setId(id)
                        .setEmail("new.email@provider.net"));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(originalUserEntity));
        when(repository.save(any(UserEntity.class))).thenReturn(updatedUserEntity);

        var result = service.updateUser(id, updateMask);

        assertUserMatchesEntity(result, updatedUserEntity);
    }

    @Test
    void updateUser_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateUser(id, testUser()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with id " + id);
    }

    @Test
    void activateUser_setsStatusToActive() {
        var id = 42L;
        UserEntity originalUserEntity =
                testUserEntity(u -> u
                        .setId(id)
                        .setStatus(DELETED));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(originalUserEntity));

        service.activateUser(id);

        var captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository).save(captor.capture());
        UserEntity savedUserEntity = captor.getValue();
        assertThat(savedUserEntity.getId()).isEqualTo(originalUserEntity.getId());
        assertThat(savedUserEntity.getUsername()).isEqualTo(originalUserEntity.getUsername());
        assertThat(savedUserEntity.getEmail()).isEqualTo(originalUserEntity.getEmail());
        assertThat(savedUserEntity.getRole()).isEqualTo(originalUserEntity.getRole());
        assertThat(savedUserEntity.getStatus()).isEqualTo(ACTIVE);
    }

    @Test
    void activateUser_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.activateUser(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with id " + id);
    }

    @Test
    void deleteUser_setsStatusToDeleted() {
        var id = 42L;
        UserEntity originalUserEntity =
                testUserEntity(u -> u
                        .setId(id)
                        .setStatus(ACTIVE));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(originalUserEntity));

        service.deleteUser(id);

        var captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository).save(captor.capture());
        UserEntity savedUserEntity = captor.getValue();
        assertThat(savedUserEntity.getId()).isEqualTo(originalUserEntity.getId());
        assertThat(savedUserEntity.getUsername()).isEqualTo(originalUserEntity.getUsername());
        assertThat(savedUserEntity.getEmail()).isEqualTo(originalUserEntity.getEmail());
        assertThat(savedUserEntity.getRole()).isEqualTo(originalUserEntity.getRole());
        assertThat(savedUserEntity.getStatus()).isEqualTo(DELETED);
    }

    @Test
    void deleteUser_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteUser(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with id " + id);
    }

    @Test
    void getUser_findsUserById() {
        var id = 42L;
        UserEntity userEntity = testUserEntity(u -> u.setId(id));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(userEntity));

        service.getUser(id);

        var captor = ArgumentCaptor.forClass(Long.class);
        verify(repository, times(1)).findById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(id);
    }

    @Test
    void getUser_returnsUser() {
        var id = 42L;
        UserEntity userEntity = testUserEntity(u -> u.setId(id));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(userEntity));

        var result = service.getUser(id);

        assertUserMatchesEntity(result, userEntity);
    }

    @Test
    void getUser_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUser(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with id " + id);
    }

    @Test
    void getUserByUsername_findsUser() {
        var username = "user";
        UserEntity userEntity = testUserEntity(u -> u.setUsername(username));

        when(repository.findByUsername(any(String.class))).thenReturn(Optional.of(userEntity));

        service.getUser(username);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(repository, times(1)).findByUsername(captor.capture());
        assertThat(captor.getValue()).isEqualTo(username);
    }

    @Test
    void getUserByUsername_returnsUser() {
        var username = "user";
        UserEntity userEntity = testUserEntity(u -> u.setUsername(username));

        when(repository.findByUsername(any(String.class))).thenReturn(Optional.of(userEntity));

        var result = service.getUser(username);

        assertUserMatchesEntity(result, userEntity);
    }

    @Test
    void getUser_throwsNotFoundException() {
        var username = "non-existent";

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUser(username))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with username '" + username + "'");
    }

    @Test
    void assignUserRole_setsNewUserRole() {
        var id = 42L;
        UserEntity originalUserEntity =
                testUserEntity(u -> u
                        .setId(id)
                        .setRole(ADMIN));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(originalUserEntity));

        service.assignUserRole(id, USER);

        var captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository, times(1)).save(captor.capture());
        UserEntity savedUserEntity = captor.getValue();
        assertThat(savedUserEntity.getId()).isEqualTo(originalUserEntity.getId());
        assertThat(savedUserEntity.getUsername()).isEqualTo(originalUserEntity.getUsername());
        assertThat(savedUserEntity.getEmail()).isEqualTo(originalUserEntity.getEmail());
        assertThat(savedUserEntity.getRole()).isEqualTo(USER);
        assertThat(savedUserEntity.getStatus()).isEqualTo(originalUserEntity.getStatus());
    }

    @Test
    void assignUserRole_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignUserRole(id, ADMIN))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with id " + id);
    }

    @Test
    void hasUserRole_whenUserHasRole_returnsTrue() {
        var id = 42L;
        UserEntity userEntity =
                testUserEntity(u -> u
                        .setId(id)
                        .setRole(ADMIN));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(userEntity));

        var result = service.hasUserRole(id, ADMIN);

        assertThat(result).isTrue();
    }

    @Test
    void hasUserRole_whenUserDoesNotHaveRole_returnsFalse() {
        var id = 42L;
        UserEntity userEntity =
                testUserEntity(u -> u
                        .setId(id)
                        .setRole(USER));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(userEntity));

        var result = service.hasUserRole(id, ADMIN);

        assertThat(result).isFalse();
    }

    @Test
    void hasUserRole_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.hasUserRole(id, ADMIN))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with id " + id);
    }

    private static User testUser() {
        return testUser(u -> {});
    }

    private static User testUser(Consumer<User.Builder> overrides) {
        var builder = User.builder()
                .id(666L)
                .username("username")
                .email("email@provider.com")
                .passwordHash("#hash")
                .status(ACTIVE)
                .role(USER);
        overrides.accept(builder);
        return builder.build();
    }

    private static UserEntity testUserEntity(Consumer<UserEntity.Builder> overrides) {
        var builder = UserEntity.builder()
                .setId(666L)
                .setUsername("username")
                .setEmail("email@provider.com")
                .setPasswordHash("#hash")
                .setStatus(ACTIVE)
                .setRole(USER);
        overrides.accept(builder);
        return builder.build();
    }

    private static void assertUserMatchesEntity(User user, UserEntity entity) {
        assertThat(user.getId()).isEqualTo(entity.getId());
        assertThat(user.getUsername()).isEqualTo(entity.getUsername());
        assertThat(user.getEmail()).isEqualTo(entity.getEmail());
        assertThat(user.getPasswordHash()).isEqualTo(entity.getPasswordHash());
        assertThat(user.getStatus()).isEqualTo(entity.getStatus());
        assertThat(user.getRole()).isEqualTo(entity.getRole());
        assertThat(user.getCreatedAt()).isEqualTo(entity.getCreatedAt());
    }
}