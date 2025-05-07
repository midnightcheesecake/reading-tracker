package com.necrock.readingtracker.user.persistence;

import com.necrock.readingtracker.user.common.UserRole;
import com.necrock.readingtracker.user.common.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

import static jakarta.persistence.GenerationType.AUTO;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @Column(unique = true)
    private String username;

    private String email;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private Instant createdAt;

    private UserEntity(Long id,
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

    @SuppressWarnings("unused") // Required for JPA
    protected UserEntity() {}

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
                .setId(id)
                .setUsername(username)
                .setEmail(email)
                .setPasswordHash(passwordHash)
                .setRole(role)
                .setStatus(status)
                .setCreatedAt(createdAt);
    }

    public static class Builder {
        private Long id;

        private String username;

        private String email;

        private String passwordHash;

        private UserRole role;

        private UserStatus status;

        private Instant createdAt;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder setRole(UserRole role) {
            this.role = role;
            return this;
        }

        public Builder setStatus(UserStatus status) {
            this.status = status;
            return this;
        }

        public Builder setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserEntity build() {
            return new UserEntity(id, username, email, passwordHash, role, status, createdAt);
        }
    }
}
