package com.necrock.readingtracker.testsupport.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.necrock.readingtracker.auth.api.dto.LoginRequest;
import com.necrock.readingtracker.auth.api.dto.RegisterRequest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class AuthTestClient {
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    private AuthTestClient(MockMvc mvc, ObjectMapper objectMapper) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    public ResultActions register(RegisterRequest request) throws Exception {
        String json = objectMapper.writeValueAsString(request);
        return mvc.perform(post("/auth/register")
                .contentType(APPLICATION_JSON)
                .content(json));
    }

    public ResultActions login(LoginRequest request) throws Exception {
        String json = objectMapper.writeValueAsString(request);
        return mvc.perform(post("/auth/login")
                .contentType(APPLICATION_JSON)
                .content(json));
    }

    public <T> T parseResponse(ResultActions resultActions, Class<T> responseType) throws Exception {
        String content = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, responseType);
    }

    @TestConfiguration
    public static class Config {
        @Bean
        public AuthTestClient authTestClient(
                MockMvc mockMvc,
                ObjectMapper objectMapper) {
            return new AuthTestClient(mockMvc, objectMapper);
        }
    }
}
