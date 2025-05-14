package com.necrock.readingtracker.testsupport.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.necrock.readingtracker.security.service.JwtService;
import com.necrock.readingtracker.testsupport.AbstractTestClient;
import com.necrock.readingtracker.user.api.admin.dto.UpdateUserRoleRequest;
import com.necrock.readingtracker.user.api.admin.dto.UpdateUserStatusRequest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class AdminUserTestClient extends AbstractTestClient<AdminUserTestClient> {

    public AdminUserTestClient(
            MockMvc mvc,
            ObjectMapper objectMapper,
            TestUserFactory testUserFactory,
            JwtService jwtService) {
        super(mvc, objectMapper, testUserFactory, jwtService);
    }

    public ResultActions getUser(long id) throws Exception {
        return get("/api/users/" + id);
    }

    public ResultActions setUserStatus(long id, UpdateUserStatusRequest request) throws Exception {
        return put("/api/users/" + id + "/status", request);
    }

    public ResultActions setUserRole(long id, UpdateUserRoleRequest request) throws Exception {
        return put("/api/users/" + id + "/role", request);
    }

    @TestConfiguration
    @Import(TestUserFactory.Config.class)
    public static class Config {

        @Bean
        public AdminUserTestClient adminUserTestClient(
                MockMvc mockMvc,
                ObjectMapper objectMapper,
                TestUserFactory testUserFactory,
                JwtService jwtService) {
            return new AdminUserTestClient(mockMvc, objectMapper, testUserFactory, jwtService);
        }
    }
}
