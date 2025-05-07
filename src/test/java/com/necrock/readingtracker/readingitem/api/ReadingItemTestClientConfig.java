package com.necrock.readingtracker.readingitem.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.necrock.readingtracker.auth.TestAuthHelper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

@TestConfiguration
public class ReadingItemTestClientConfig {

    @Bean
    public ReadingItemTestClient readingItemTestClient(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            TestAuthHelper testAuthHelper) {
        return new ReadingItemTestClient(mockMvc, objectMapper, testAuthHelper);
    }
}
