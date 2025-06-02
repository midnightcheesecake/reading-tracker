package com.necrock.readingtracker.testsupport.readingProgress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.necrock.readingtracker.readingprogress.api.dto.CreateReadingProgressRequest;
import com.necrock.readingtracker.readingprogress.api.dto.UpdateReadingProgressRequest;
import com.necrock.readingtracker.security.service.JwtService;
import com.necrock.readingtracker.testsupport.AbstractTestClient;
import com.necrock.readingtracker.testsupport.user.TestUserFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class ReadingProgressTestClient extends AbstractTestClient<ReadingProgressTestClient> {

    private ReadingProgressTestClient(
            MockMvc mvc,
            ObjectMapper objectMapper,
            TestUserFactory testUserFactory,
            JwtService jwtService) {
        super(mvc, objectMapper, testUserFactory, jwtService);
    }

    public ResultActions addReadingProgress(CreateReadingProgressRequest dto) throws Exception {
        return post("/api/progress", dto);
    }

    public ResultActions updateReadingProgress(long readingItemId, UpdateReadingProgressRequest dto) throws Exception {
        return patch("/api/progress/" + readingItemId, dto);
    }

    public ResultActions deleteReadingProgress(long readingItemId) throws Exception {
        return delete("/api/progress/" + readingItemId);
    }

    public ResultActions getReadingProgress(long readingItemId) throws Exception {
        return get("/api/progress/" + readingItemId);
    }

    public ResultActions listReadingProgress() throws Exception {
        return get("/api/progress");
    }

    @TestConfiguration
    @Import(TestUserFactory.Config.class)
    public static class Config {
        @Bean
        public ReadingProgressTestClient readingProgressTestClient(
                MockMvc mockMvc,
                ObjectMapper objectMapper,
                TestUserFactory testUserFactory,
                JwtService jwtService) {
            return new ReadingProgressTestClient(mockMvc, objectMapper, testUserFactory, jwtService);
        }
    }
}
