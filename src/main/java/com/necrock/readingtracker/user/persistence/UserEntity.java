package com.necrock.readingtracker.user.persistence;

import com.necrock.readingtracker.user.common.UserRole;
import com.necrock.readingtracker.user.common.UserStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.Objects;

import static jakarta.persistence.GenerationType.AUTO;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(
                name = "user_unique_username",
                columnNames = {"username"}))
public class UserEntity {

    public static final String UNIQUE_USERNAME =
            "user_unique_username";

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserEntity that = (UserEntity) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getUsername(), that.getUsername())
                && Objects.equals(getEmail(), that.getEmail())
                && Objects.equals(getPasswordHash(), that.getPasswordHash())
                && getRole() == that.getRole()
                && getStatus() == that.getStatus()
                && Objects.equals(getCreatedAt(), that.getCreatedAt());
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

        public UserEntity build() {
            return new UserEntity(id, username, email, passwordHash, role, status, createdAt);
        }
    }
}
