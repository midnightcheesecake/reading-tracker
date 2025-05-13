package com.necrock.readingtracker.testsupport.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.necrock.readingtracker.testsupport.auth.TestAuthHelper;
import com.necrock.readingtracker.user.api.admin.dto.UpdateUserRoleRequest;
import com.necrock.readingtracker.user.api.admin.dto.UpdateUserStatusRequest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class AdminUserTestClient {
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;
    private final TestAuthHelper testAuthHelper;

    public AdminUserTestClient(MockMvc mvc, ObjectMapper objectMapper, TestAuthHelper testAuthHelper) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
        this.testAuthHelper = testAuthHelper;
    }

    public ResultActions getUser(long id) throws Exception {
        String token = testAuthHelper.createUserAndGetToken("testUser", "password");
        return mvc.perform(get("/api/users/" + id)
                .header("Authorization", "Bearer " + token));
    }

    public ResultActions setUserStatus(long id, UpdateUserStatusRequest request) throws Exception {
        String token = testAuthHelper.createUserAndGetToken("testUser", "password");
        String json = objectMapper.writeValueAsString(request);
        return mvc.perform(put("/api/users/" + id + "/status")
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(json));
    }

    public ResultActions setUserRole(long id, UpdateUserRoleRequest request) throws Exception {
        String token = testAuthHelper.createUserAndGetToken("testUser", "password");
        String json = objectMapper.writeValueAsString(request);
        return mvc.perform(put("/api/users/" + id + "/role")
                .header("Authorization", "Bearer " + token)
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
        public AdminUserTestClient adminUserTestClient(
                MockMvc mockMvc,
                ObjectMapper objectMapper,
                TestAuthHelper testAuthHelper) {
            return new AdminUserTestClient(mockMvc, objectMapper, testAuthHelper);
        }
    }
}
