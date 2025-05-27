package com.necrock.readingtracker.user.service.model;

import com.necrock.readingtracker.user.common.UserRole;
import com.necrock.readingtracker.user.common.UserStatus;

import java.time.Instant;
import java.util.Objects;

public class User {
    private final Long id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final UserRole role;
    private final UserStatus status;
    private final Instant createdAt;

    private User(Long id,
                 String username,
                 String email,
                 String passwordHash,
                 UserRole role,
                 UserStatus status,
                 Instant createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Builder toBuilder() {
        return builder()
                .id(id)
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .role(role)
                .status(status)
                .createdAt(createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;
        return Objects.equals(getId(), user.getId())
                && Objects.equals(getUsername(), user.getUsername())
                && Objects.equals(getEmail(), user.getEmail())
                && Objects.equals(getPasswordHash(), user.getPasswordHash())
                && getRole() == user.getRole()
                && getStatus() == user.getStatus()
                && Objects.equals(getCreatedAt(), user.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(),
                getUsername(),
                getEmail(),
                getPasswordHash(),
                getRole(),
                getStatus(),
                getCreatedAt());
    }

    public static class Builder {
        private Long id;
        private String username;
        private String email;
        private String passwordHash;
        private UserRole role;
        private UserStatus status;
        private Instant createdAt;

        public Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder role(UserRole role) {
            this.role = role;
            return this;
        }

        public Builder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public User build() {
            return new User(id, username, email, passwordHash, role, status, createdAt);
        }
    }
}
