package com.necrock.readingtracker.configuration;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.Base64;

@Configuration
public class SecurityConfig {

    @Value("${app.signingkey}")
    private String base64Key;

    @Bean
    public Key jwtSigningKey() {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        return Keys.hmacShaKeyFor(decodedKey);
    }
}
