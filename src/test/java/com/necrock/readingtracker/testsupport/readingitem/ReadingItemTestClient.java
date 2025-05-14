package com.necrock.readingtracker.testsupport.readingitem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.necrock.readingtracker.security.service.JwtService;
import com.necrock.readingtracker.testsupport.AbstractTestClient;
import com.necrock.readingtracker.readingitem.api.dto.CreateReadingItemDto;
import com.necrock.readingtracker.readingitem.api.dto.UpdateReadingItemDto;
import com.necrock.readingtracker.testsupport.user.TestUserFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class ReadingItemTestClient extends AbstractTestClient<ReadingItemTestClient> {

    public ReadingItemTestClient(
            MockMvc mvc,
            ObjectMapper objectMapper,
            TestUserFactory testUserFactory,
            JwtService jwtService) {
        super(mvc, objectMapper, testUserFactory, jwtService);
    }

    public ResultActions addReadingItem(CreateReadingItemDto dto) throws Exception {
        return post("/api/items", dto);
    }

    public ResultActions updateReadingItem(long id, UpdateReadingItemDto dto) throws Exception {
        return patch("/api/items/" + id, dto);
    }

    public ResultActions deleteReadingItem(long id) throws Exception {
        return delete("/api/items/" + id);
    }

    public ResultActions getReadingItem(long id) throws Exception {
        return get("/api/items/" + id);
    }

    public ResultActions listReadingItems() throws Exception {
        return get("/api/items");
    }

    @TestConfiguration
    @Import(TestUserFactory.Config.class)
    public static class Config {
        @Bean
        public ReadingItemTestClient readingItemTestClient(
                MockMvc mockMvc,
                ObjectMapper objectMapper,
                TestUserFactory testUserFactory,
                JwtService jwtService) {
            return new ReadingItemTestClient(mockMvc, objectMapper, testUserFactory, jwtService);
        }
    }
}
