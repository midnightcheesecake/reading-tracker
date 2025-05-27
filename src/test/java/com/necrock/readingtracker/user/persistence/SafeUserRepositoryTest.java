package com.necrock.readingtracker.user.persistence;

import com.necrock.readingtracker.exception.AlreadyExistsException;
import com.necrock.readingtracker.user.common.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Transactional
@DataJpaTest
@Import(SafeUserRepository.class)
class SafeUserRepositoryTest {

    @Autowired
    private SafeUserRepository repository;

    @Autowired
    private UserRepository unsafeRepository;

    @BeforeEach
    void setUp() {
        unsafeRepository.deleteAll();
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
        UserEntity user = UserEntity.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .role(role).build();

        var savedUser = repository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(username);
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getPasswordHash()).isEqualTo(passwordHash);
        assertThat(savedUser.getRole()).isEqualTo(role);
    }

    @Test
    void save_increasesCount() {
        long initialCount = unsafeRepository.count();

        repository.save(UserEntity.builder().username("newUser").build());

        long finalCount = unsafeRepository.count();
        assertThat(finalCount).isEqualTo(initialCount + 1);
    }

    @Test
    void save_withExistingUsername_fails() {
        var duplicateUsername = "duplicateUser";
        UserEntity user1 = UserEntity.builder().username(duplicateUsername).email("email1").build();
        UserEntity user2 = UserEntity.builder().username(duplicateUsername).email("email2").build();

        repository.saveAndFlush(user1);

        assertThatThrownBy(() -> repository.saveAndFlush(user2))
                .isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    void findById_withSavedUserId_returnsUser() {
        UserEntity user = UserEntity.builder()
                .username("user")
                .email("email@provider.com")
                .passwordHash("#hash")
                .role(UserRole.USER)
                .build();

        var savedUser = repository.save(user);
        var foundOptionalUser = repository.findById(savedUser.getId());

        assertThat(foundOptionalUser).isNotEmpty();
        assertThat(foundOptionalUser).hasValueSatisfying(foundUser -> {
            assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
            assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
            assertThat(foundUser.getPasswordHash()).isEqualTo(user.getPasswordHash());
            assertThat(foundUser.getRole()).isEqualTo(user.getRole());
        });
    }

    @Test
    void findById_withNonexistentId_returnsEmptyOptional() {
        var notFoundUser = repository.findById(1L);

        assertThat(notFoundUser).isEmpty();
    }

    @Test
    void findByUsername_withSavedUsername_returnsUser() {
        UserEntity user = UserEntity.builder()
                .username("user")
                .email("email@provider.com")
                .passwordHash("#hash")
                .role(UserRole.USER)
                .build();

        repository.save(user);
        var foundOptionalUser = repository.findByUsername(user.getUsername());

        assertThat(foundOptionalUser).isNotEmpty();
        assertThat(foundOptionalUser).hasValueSatisfying(foundUser -> {
            assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
            assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
            assertThat(foundUser.getPasswordHash()).isEqualTo(user.getPasswordHash());
            assertThat(foundUser.getRole()).isEqualTo(user.getRole());
        });
    }

    @Test
    void findByUsername_withNonexistentUsername_returnsEmptyOptional() {
        var notFoundUser = repository.findByUsername("none");

        assertThat(notFoundUser).isEmpty();
    }
}