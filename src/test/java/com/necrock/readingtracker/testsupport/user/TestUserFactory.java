package com.necrock.readingtracker.testsupport.user;

import com.necrock.readingtracker.user.common.UserRole;
import com.necrock.readingtracker.user.common.UserStatus;
import com.necrock.readingtracker.user.persistence.UserEntity;
import com.necrock.readingtracker.user.persistence.UserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;

public class TestUserFactory {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    private TestUserFactory(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            Clock clock) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    public UserEntity createUser(String username) {
        return createUser(username, UserRole.USER, UserStatus.ACTIVE);
    }

    public UserEntity createAdmin(String username) {
        return createUser(username, UserRole.ADMIN, UserStatus.ACTIVE);
    }

    public UserEntity createUser(String username, UserRole role, UserStatus status) {
        var maybeUser = repository.findByUsername(username);
        return maybeUser.map(userEntity -> overrideExistingUser(userEntity, role, status))
                .orElseGet(() -> createNewUser(username, role, status));
    }

    private UserEntity createNewUser(String username, UserRole role, UserStatus status) {
        UserEntity user = UserEntity.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode("testPass123"))
                .email(username + "@test.com")
                .role(role)
                .status(status)
                .createdAt(Instant.now(clock))
                .build();
        return repository.save(user);
    }

    private UserEntity overrideExistingUser(UserEntity existingUser, UserRole role, UserStatus status) {
        UserEntity user = UserEntity.builder()
                .id(existingUser.getId())
                .username(existingUser.getUsername())
                .passwordHash(existingUser.getPasswordHash())
                .email(existingUser.getEmail())
                .role(role)
                .status(status)
                .createdAt(existingUser.getCreatedAt())
                .build();
        return repository.save(user);
    }

    @TestConfiguration
    public static class Config {
        @Bean
        public TestUserFactory testUserFactory(
                UserRepository repository,
                PasswordEncoder passwordEncoder,
                Clock clock) {
            return new TestUserFactory(repository, passwordEncoder, clock);
        }
    }
}
