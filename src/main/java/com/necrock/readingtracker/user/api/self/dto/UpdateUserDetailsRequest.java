package com.necrock.readingtracker.user.api.self.dto;

public class UpdateUserDetailsRequest {
    private final String email;

    private UpdateUserDetailsRequest(String email) {
        this.email = email;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getEmail() {
        return email;
    }

    public static class Builder {
        private String email;

        private Builder() {}

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public UpdateUserDetailsRequest build() {
            return new UpdateUserDetailsRequest(email);
        }
    }
}
