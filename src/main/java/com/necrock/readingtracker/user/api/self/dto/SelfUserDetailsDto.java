package com.necrock.readingtracker.user.api.self.dto;

public class SelfUserDetailsDto {

    private final String username;

    private final String email;

    private SelfUserDetailsDto(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public static class Builder {
        private String username;
        private String email;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public SelfUserDetailsDto build() {
            return new SelfUserDetailsDto(username, email);
        }
    }
}
