package com.necrock.readingtracker.user.service;

import com.necrock.readingtracker.testsupport.configuration.TestTimeConfig;
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
    void addUser_returnsSavedUser() {
        var username = "user";
        var email = "email@provider.com";
        var passwordHash = "#hash";
        User toSaveUser = User.builder()
                .username(username).email(email).passwordHash(passwordHash)
                .build();
        UserEntity savedUserEntity = UserEntity.builder()
                .id(42L)
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .status(ACTIVE)
                .role(USER)
                .createdAt(TestTimeConfig.NOW)
                .build();

        when(repository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        var result = service.addUser(toSaveUser);

        assertUserMatchesEntity(result, savedUserEntity);
    }

    @Test
    void addUser_withExistingUsername_throwsAlreadyExistsException() {
        var username = "username";

        when(repository.save(any(UserEntity.class)))
                .thenThrow(new DataIntegrityViolationException(UserEntity.UNIQUE_USERNAME));

        assertThatThrownBy(() -> service.addUser(testUser(u -> u.username(username))))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage("User with username '" + username + "' already exists");
    }

    @Test
    void updateUser_appliesChanges() {
        var id = 42L;
        UserEntity originalUserEntity =
                testUserEntity(u -> u
                        .id(id)
                        .email("old.email@provider.com"));
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
        assertThat(savedUserEntity.getPasswordHash()).isEqualTo(originalUserEntity.getPasswordHash());
        assertThat(savedUserEntity.getRole()).isEqualTo(originalUserEntity.getRole());
        assertThat(savedUserEntity.getStatus()).isEqualTo(originalUserEntity.getStatus());
    }

    @Test
    void updateUser_returnsNewlySavedUser() {
        var id = 42L;
        UserEntity originalUserEntity =
                testUserEntity(u -> u
                        .id(id)
                        .email("old.email@provider.com"));
        User updateMask = User.builder().email("new.email@provider.net").build();
        UserEntity updatedUserEntity =
                testUserEntity(u -> u
                        .id(id)
                        .email("new.email@provider.net"));

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
    void getUser_withId_findsUserById() {
        var id = 42L;
        UserEntity userEntity = testUserEntity(u -> u.id(id));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(userEntity));

        service.getUser(id);

        var captor = ArgumentCaptor.forClass(Long.class);
        verify(repository, times(1)).findById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(id);
    }

    @Test
    void getUser_withId_returnsUser() {
        var id = 42L;
        UserEntity userEntity = testUserEntity(u -> u.id(id));

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
    void getUser_withUsername_findsUser() {
        var username = "user";
        UserEntity userEntity = testUserEntity(u -> u.username(username));

        when(repository.findByUsername(any(String.class))).thenReturn(Optional.of(userEntity));

        service.getUser(username);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(repository).findByUsername(captor.capture());
        assertThat(captor.getValue()).isEqualTo(username);
    }

    @Test
    void getUser_withUsername_returnsUser() {
        var username = "user";
        UserEntity userEntity = testUserEntity(u -> u.username(username));

        when(repository.findByUsername(any(String.class))).thenReturn(Optional.of(userEntity));

        var result = service.getUser(username);

        assertUserMatchesEntity(result, userEntity);
    }

    @Test
    void getUser_withUnknownUsername_throwsNotFoundException() {
        var username = "non-existent";

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUser(username))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with username '" + username + "'");
    }

    @Test
    void setPassword_setsNewPassword() {
        var id = 42L;
        var newPasswordHash = "#newPasswordHash";
        UserEntity originalUserEntity =
                testUserEntity(u -> u
                        .id(id)
                        .status(DELETED));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(originalUserEntity));

        service.setPassword(id, newPasswordHash);

        var captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository).save(captor.capture());
        UserEntity savedUserEntity = captor.getValue();
        assertThat(savedUserEntity.getId()).isEqualTo(originalUserEntity.getId());
        assertThat(savedUserEntity.getUsername()).isEqualTo(originalUserEntity.getUsername());
        assertThat(savedUserEntity.getPasswordHash()).isEqualTo(newPasswordHash);
        assertThat(savedUserEntity.getEmail()).isEqualTo(originalUserEntity.getEmail());
        assertThat(savedUserEntity.getRole()).isEqualTo(originalUserEntity.getRole());
        assertThat(savedUserEntity.getStatus()).isEqualTo(originalUserEntity.getStatus());
    }

    @Test
    void setPassword_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setPassword(id, "#newHash"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with id " + id);
    }

    @Test
    void setUserStatus_setsNewUserStatus() {
        var id = 42L;
        UserEntity originalUserEntity =
                testUserEntity(u -> u
                        .id(id)
                        .status(DELETED));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(originalUserEntity));

        service.setUserStatus(id, ACTIVE);

        var captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository).save(captor.capture());
        UserEntity savedUserEntity = captor.getValue();
        assertThat(savedUserEntity.getId()).isEqualTo(originalUserEntity.getId());
        assertThat(savedUserEntity.getUsername()).isEqualTo(originalUserEntity.getUsername());
        assertThat(savedUserEntity.getEmail()).isEqualTo(originalUserEntity.getEmail());
        assertThat(savedUserEntity.getPasswordHash()).isEqualTo(originalUserEntity.getPasswordHash());
        assertThat(savedUserEntity.getRole()).isEqualTo(originalUserEntity.getRole());
        assertThat(savedUserEntity.getStatus()).isEqualTo(ACTIVE);
    }

    @Test
    void setUserStatus_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setUserStatus(id, ACTIVE))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with id " + id);
    }

    @Test
    void setUserRole_setsNewUserRole() {
        var id = 42L;
        UserEntity originalUserEntity =
                testUserEntity(u -> u
                        .id(id)
                        .role(ADMIN));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(originalUserEntity));

        service.setUserRole(id, USER);

        var captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(repository, times(1)).save(captor.capture());
        UserEntity savedUserEntity = captor.getValue();
        assertThat(savedUserEntity.getId()).isEqualTo(originalUserEntity.getId());
        assertThat(savedUserEntity.getUsername()).isEqualTo(originalUserEntity.getUsername());
        assertThat(savedUserEntity.getEmail()).isEqualTo(originalUserEntity.getEmail());
        assertThat(savedUserEntity.getPasswordHash()).isEqualTo(originalUserEntity.getPasswordHash());
        assertThat(savedUserEntity.getRole()).isEqualTo(USER);
        assertThat(savedUserEntity.getStatus()).isEqualTo(originalUserEntity.getStatus());
    }

    @Test
    void setUserRole_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setUserRole(id, ADMIN))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with id " + id);
    }

    @Test
    void hasUserRole_whenUserHasRole_returnsTrue() {
        var id = 42L;
        UserEntity userEntity =
                testUserEntity(u -> u
                        .id(id)
                        .role(ADMIN));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(userEntity));

        var result = service.hasUserRole(id, ADMIN);

        assertThat(result).isTrue();
    }

    @Test
    void hasUserRole_whenUserDoesNotHaveRole_returnsFalse() {
        var id = 42L;
        UserEntity userEntity =
                testUserEntity(u -> u
                        .id(id)
                        .role(USER));

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
                .id(666L)
                .username("username")
                .email("email@provider.com")
                .passwordHash("#hash")
                .status(ACTIVE)
                .role(USER);
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