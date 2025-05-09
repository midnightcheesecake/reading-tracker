package com.necrock.readingtracker.auth.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

@TestConfiguration
public class AuthTestClientConfig {

    @Bean
    public AuthTestClient authTestClient(
            MockMvc mockMvc,
            ObjectMapper objectMapper) {
        return new AuthTestClient(mockMvc, objectMapper);
    }
}
