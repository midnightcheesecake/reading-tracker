package com.necrock.readingtracker.testsupport.readingitem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.necrock.readingtracker.testsupport.auth.TestAuthHelper;
import com.necrock.readingtracker.readingitem.api.dto.CreateReadingItemDto;
import com.necrock.readingtracker.readingitem.api.dto.UpdateReadingItemDto;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ReadingItemTestClient {
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;
    private final TestAuthHelper testAuthHelper;

    public ReadingItemTestClient(MockMvc mvc, ObjectMapper objectMapper, TestAuthHelper testAuthHelper) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
        this.testAuthHelper = testAuthHelper;
    }

    public ResultActions addReadingItem(CreateReadingItemDto dto) throws Exception {
        String token = testAuthHelper.createUserAndGetToken("testUser", "password");
        String json = objectMapper.writeValueAsString(dto);
        return mvc.perform(post("/api/items")
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(json));
    }

    public ResultActions updateReadingItem(long id, UpdateReadingItemDto dto) throws Exception {
        String token = testAuthHelper.createUserAndGetToken("testUser", "password");
        String json = objectMapper.writeValueAsString(dto);
        return mvc.perform(patch("/api/items/" + id)
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(json));
    }

    public ResultActions deleteReadingItem(long id) throws Exception {
        String token = testAuthHelper.createUserAndGetToken("testUser", "password");
        return mvc.perform(delete("/api/items/" + id)
                .header("Authorization", "Bearer " + token));
    }

    public ResultActions getReadingItem(long id) throws Exception {
        String token = testAuthHelper.createUserAndGetToken("testUser", "password");
        return mvc.perform(get("/api/items/" + id)
                .header("Authorization", "Bearer " + token));
    }

    public ResultActions listReadingItems() throws Exception {
        String token = testAuthHelper.createUserAndGetToken("testUser", "password");
        return mvc.perform(get("/api/items")
                .header("Authorization", "Bearer " + token));
    }

    public <T> T parseResponse(ResultActions resultActions, Class<T> responseType) throws Exception {
        String content = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, responseType);
    }

    @TestConfiguration
    public static class Config {
        @Bean
        public ReadingItemTestClient readingItemTestClient(
                MockMvc mockMvc,
                ObjectMapper objectMapper,
                TestAuthHelper testAuthHelper) {
            return new ReadingItemTestClient(mockMvc, objectMapper, testAuthHelper);
        }
    }
}
