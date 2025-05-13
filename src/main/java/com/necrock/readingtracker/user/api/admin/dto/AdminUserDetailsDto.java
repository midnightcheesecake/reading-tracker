package com.necrock.readingtracker.user.api.admin.dto;

import com.necrock.readingtracker.user.common.UserRole;
import com.necrock.readingtracker.user.common.UserStatus;

public class AdminUserDetailsDto {
    private final Long id;
    private final String username;
    private final UserRole role;
    private final UserStatus status;

    private AdminUserDetailsDto(Long id, String username, UserRole role, UserStatus status) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public static class Builder {
        private Long id;
        private String username;
        private UserRole role;
        private UserStatus status;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
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

        public AdminUserDetailsDto build() {
            return new AdminUserDetailsDto(id, username, role, status);
        }
    }
}
