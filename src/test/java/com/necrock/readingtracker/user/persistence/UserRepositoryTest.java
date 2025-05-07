package com.necrock.readingtracker.user.persistence;

import com.necrock.readingtracker.user.common.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void save_withUser_setsIdField() {
        UserEntity user = UserEntity.builder().build();

        var savedUser = repository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    void save_withUser_keepsNonIdFields() {
        var username = "user";
        var email = "email@provider.com";
        var passwordHash = "#hash";
        var role = UserRole.USER;
        UserEntity user = UserEntity.builder().setUsername(username).setEmail(email).setPasswordHash(passwordHash).setRole(role).build();

        var savedUser = repository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(username);
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getPasswordHash()).isEqualTo(passwordHash);
        assertThat(savedUser.getRole()).isEqualTo(role);
    }

    @Test
    void save_increasesCount() {
        long initialCount = repository.count();

        repository.save(UserEntity.builder().setUsername("newUser").build());

        long finalCount = repository.count();
        assertThat(finalCount).isEqualTo(initialCount + 1);
    }

    @Test
    void save_withExistingUsername_fails() {
        var duplicateUsername = "duplicateUser";
        UserEntity user1 = UserEntity.builder().setUsername(duplicateUsername).setEmail("email1").build();
        UserEntity user2 = UserEntity.builder().setUsername(duplicateUsername).setEmail("email2").build();

        repository.save(user1);

        assertThatThrownBy(() -> repository.save(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findById_withNonexistentId_returnsEmptyOptional() {
        var notFoundUser = repository.findById(1L);

        assertThat(notFoundUser).isEmpty();
    }

    @SuppressWarnings("ConstantConditions") // Asserting foundUser is not null before checking fields
    @Test
    void findById_withSavedUserId_returnsUser() {
        var username = "user";
        var email = "email@provider.com";
        var passwordHash = "#hash";
        var role = UserRole.USER;
        UserEntity user = UserEntity.builder().setUsername(username).setEmail(email).setPasswordHash(passwordHash).setRole(role).build();

        var savedUser = repository.save(user);
        var foundUser = repository.findById(savedUser.getId()).orElse(null);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(username);
        assertThat(foundUser.getEmail()).isEqualTo(email);
        assertThat(foundUser.getPasswordHash()).isEqualTo(passwordHash);
        assertThat(foundUser.getRole()).isEqualTo(role);
    }

    @Test
    void findByUsername_withNonexistentUsername_returnsEmptyOptional() {
        var notFoundUser = repository.findByUsername("none");

        assertThat(notFoundUser).isEmpty();
    }

    @SuppressWarnings("ConstantConditions") // Asserting foundUser is not null before checking fields
    @Test
    void findByUsername_withSavedUsername_returnsUser() {
        var username = "user";
        var email = "email@provider.com";
        var passwordHash = "#hash";
        var role = UserRole.USER;
        UserEntity user = UserEntity.builder().setUsername(username).setEmail(email).setPasswordHash(passwordHash).setRole(role).build();

        repository.save(user);
        var foundUser = repository.findByUsername(username).orElse(null);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(username);
        assertThat(foundUser.getEmail()).isEqualTo(email);
        assertThat(foundUser.getPasswordHash()).isEqualTo(passwordHash);
        assertThat(foundUser.getRole()).isEqualTo(role);
    }
}