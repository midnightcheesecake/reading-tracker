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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    private TestUserFactory(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            Clock clock) {
        this.userRepository = userRepository;
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
        var maybeUser = userRepository.findByUsername(username);
        return maybeUser.map(userEntity -> overrideExistingUser(userEntity, role, status))
                .orElseGet(() -> createNewUser(username, role, status));
    }

    private UserEntity createNewUser(String username, UserRole role, UserStatus status) {
        UserEntity user = UserEntity.builder()
                .setUsername(username)
                .setPasswordHash(passwordEncoder.encode("testPass123"))
                .setEmail(username + "@test.com")
                .setRole(role)
                .setStatus(status)
                .setCreatedAt(Instant.now(clock))
                .build();
        return userRepository.save(user);
    }

    private UserEntity overrideExistingUser(UserEntity existingUser, UserRole role, UserStatus status) {
        UserEntity user = UserEntity.builder()
                .setId(existingUser.getId())
                .setUsername(existingUser.getUsername())
                .setPasswordHash(existingUser.getPasswordHash())
                .setEmail(existingUser.getEmail())
                .setRole(role)
                .setStatus(status)
                .setCreatedAt(existingUser.getCreatedAt())
                .build();
        return userRepository.save(user);
    }

    @TestConfiguration
    public static class Config {
        @Bean
        public TestUserFactory testUserFactory(
                UserRepository userRepository,
                PasswordEncoder passwordEncoder,
                Clock clock) {
            return new TestUserFactory(userRepository, passwordEncoder, clock);
        }
    }
}
