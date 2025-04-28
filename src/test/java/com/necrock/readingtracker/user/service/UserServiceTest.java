package com.necrock.readingtracker.user.service;

import com.necrock.readingtracker.configuration.TestTimeConfig;
import com.necrock.readingtracker.exception.AlreadyExistsException;
import com.necrock.readingtracker.exception.NotFoundException;
import com.necrock.readingtracker.user.persistence.User;
import com.necrock.readingtracker.user.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static com.necrock.readingtracker.user.persistence.UserRole.ADMIN;
import static com.necrock.readingtracker.user.persistence.UserRole.USER;
import static com.necrock.readingtracker.user.persistence.UserStatus.ACTIVE;
import static com.necrock.readingtracker.user.persistence.UserStatus.DELETED;
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
        var username = "user";
        var email = "email@provider.com";
        var passwordHash = "#hash";
        User toSave = User.builder()
                // input values
                .username(username).email(email).passwordHash(passwordHash)
                .build();
        User savedUser = User.builder()
                // input values
                .username(username).email(email).passwordHash(passwordHash)
                // default values
                .status(ACTIVE).role(USER).createdAt(TestTimeConfig.NOW)
                .build();

        service.addUser(toSave);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(repository, times(1)).save(captor.capture());
        assertThat(captor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(savedUser);
    }

    @Test
    void assUser_returnsSavedUser() {
        var username = "user";
        var email = "email@provider.com";
        var passwordHash = "#hash";
        User toSave = User.builder()
                .username(username).email(email).passwordHash(passwordHash)
                .build();
        User savedUser = User.builder()
                .id(42L)
                .username(username).email(email).passwordHash(passwordHash)
                .status(ACTIVE).role(USER).createdAt(TestTimeConfig.NOW)
                .build();
        when(repository.save(any(User.class))).thenReturn(savedUser);

        var result = service.addUser(toSave);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(savedUser);
    }

    @Test
    void addUser_withExistingUsername_throwsAlreadyExistsException() {
        var username = "username";

        when(repository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.addUser(testUserBuilder().username(username).build()))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage("User with username '" + username + "' already exists");
    }

    @Test
    void updateUser_savesUser() {
        var id = 42L;
        var oldEmail = "old.email@provider.com";
        var newEmail = "new.email@provider.net";
        User old = testUserBuilder().id(id).email(oldEmail).build();
        User updateMask = User.builder().email(newEmail).build();
        User updated = testUserBuilder().id(id).email(newEmail).build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(old));

        service.updateUser(id, updateMask);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(repository, times(1)).save(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(updated);
    }

    @Test
    void updateUser_returnsNewlySavedUser() {
        var id = 42L;
        var oldEmail = "old.email@provider.com";
        var newEmail = "new.email@provider.net";
        User old = testUserBuilder().id(id).email(oldEmail).build();
        User updateMask = User.builder().email(newEmail).build();
        User updated = testUserBuilder().id(id).email(newEmail).build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(old));
        when(repository.save(any(User.class))).thenReturn(updated);

        var result = service.updateUser(id, updateMask);

        assertThat(result).usingRecursiveComparison().isEqualTo(updated);
    }

    @Test
    void updateUser_withUnknownId_throwsNotFoundException() {
        var id = 42L;

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateUser(id, testUserBuilder().build()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with id " + id);
    }

    @Test
    void activateUser_setsStatusToActive() {
        var id = 42L;
        User old = testUserBuilder().id(id).status(DELETED).build();
        User updated = testUserBuilder().id(id).status(ACTIVE).build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(old));

        service.activateUser(id);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(repository, times(1)).save(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(updated);
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
        User old = testUserBuilder().id(id).status(ACTIVE).build();
        User updated = testUserBuilder().id(id).status(DELETED).build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(old));

        service.deleteUser(id);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(repository, times(1)).save(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(updated);
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
        User user = testUserBuilder().id(id).build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(user));

        service.getUser(id);

        var captor = ArgumentCaptor.forClass(Long.class);
        verify(repository, times(1)).findById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(id);
    }

    @Test
    void getUser_returnsUser() {
        var id = 42L;
        User user = testUserBuilder().id(id).build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(user));

        var result = service.getUser(id);

        assertThat(result).isEqualTo(user);
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
    void findUserByUsername_findsUserByUsername() {
        var username = "user";
        User user = testUserBuilder().username(username).build();
        when(repository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        service.findUserByUsername(username);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(repository, times(1)).findByUsername(captor.capture());
        assertThat(captor.getValue()).isEqualTo(username);
    }

    @Test
    void findUserByUsername_returnsUser() {
        var username = "user";
        User user = testUserBuilder().username(username).build();
        when(repository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        var result = service.findUserByUsername(username);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void findUserByUsername_withUnknownUsername_throwsNotFoundException() {
        var username = "non-existent";

        when(repository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findUserByUsername(username))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with username '" + username + "'");
    }

    @Test
    void assignUserRole_setsNewUserRole() {
        var id = 42L;
        User old = testUserBuilder().id(id).role(ADMIN).build();
        User updated = testUserBuilder().id(id).role(USER).build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(old));

        service.assignUserRole(id, USER);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(repository, times(1)).save(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(updated);
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
        User user = testUserBuilder().id(id).role(ADMIN).build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(user));

        var result = service.hasUserRole(id, ADMIN);

        assertThat(result).isTrue();
    }

    @Test
    void hasUserRole_whenUserDoesNotHaveRole_returnsFalse() {
        var id = 42L;
        User user = testUserBuilder().id(id).role(USER).build();
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(user));

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

    private static User.Builder testUserBuilder() {
        return User.builder()
                .id(666L)
                .username("username")
                .email("email@provider.com")
                .passwordHash("#hash")
                .status(ACTIVE)
                .role(USER);
    }
}