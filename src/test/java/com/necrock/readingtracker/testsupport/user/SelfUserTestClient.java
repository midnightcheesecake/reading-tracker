package com.necrock.readingtracker.testsupport.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.necrock.readingtracker.security.service.JwtService;
import com.necrock.readingtracker.testsupport.AbstractTestClient;
import com.necrock.readingtracker.user.api.self.dto.UpdatePasswordRequest;
import com.necrock.readingtracker.user.api.self.dto.UpdateUserDetailsRequest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class SelfUserTestClient extends AbstractTestClient<SelfUserTestClient> {

    private SelfUserTestClient(
            MockMvc mvc,
            ObjectMapper objectMapper,
            TestUserFactory testUserFactory,
            JwtService jwtService) {
        super(mvc, objectMapper, testUserFactory, jwtService);
    }

    public ResultActions getUser() throws Exception{
        return get("/api/me");
    }

    public ResultActions updateUser(UpdateUserDetailsRequest request) throws Exception {
        return patch("/api/me", request);
    }

    public ResultActions setNewPassword(UpdatePasswordRequest request) throws Exception {
        return put("/api/me/password", request);
    }

    public ResultActions deleteUser() throws Exception {
        return delete("/api/me");
    }

    @TestConfiguration
    @Import(TestUserFactory.Config.class)
    public static class Config {

        @Bean
        public SelfUserTestClient selfUserTestClient(
                MockMvc mockMvc,
                ObjectMapper objectMapper,
                TestUserFactory testUserFactory,
                JwtService jwtService) {
            return new SelfUserTestClient(mockMvc, objectMapper, testUserFactory, jwtService);
        }
    }
}
