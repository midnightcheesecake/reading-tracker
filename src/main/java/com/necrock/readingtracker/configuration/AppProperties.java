package com.necrock.readingtracker.configuration;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * Timezone to use in the application.
     */
    private String timezone = "UTC";

    /**
     * Base64-encoded signing key for JWT.
     */
    @NotBlank(message = "The signing key must not be blank")
    private String signingKey;

    public String getTimezone() {
        return timezone;
    }

    @SuppressWarnings("unused") // Used by Spring when reading application properties
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getSigningKey() {
        return signingKey;
    }

    @SuppressWarnings("unused") // Used by Spring when reading application properties
    public void setSigningKey(String signingKey) {
        this.signingKey = signingKey;
    }
}
