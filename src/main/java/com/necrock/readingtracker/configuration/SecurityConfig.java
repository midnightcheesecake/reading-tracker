package com.necrock.readingtracker.configuration;

import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.Base64;

@Configuration
public class SecurityConfig {

    private final AppProperties properties;

    public SecurityConfig(AppProperties properties) {
        this.properties = properties;
    }

    @Bean
    public Key jwtSigningKey() {
        byte[] decodedKey = Base64.getDecoder().decode(properties.getSigningKey());
        return Keys.hmacShaKeyFor(decodedKey);
    }
}
