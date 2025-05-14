package com.necrock.readingtracker.testsupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.necrock.readingtracker.security.service.JwtService;
import com.necrock.readingtracker.testsupport.user.TestUserFactory;
import com.necrock.readingtracker.user.service.model.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public abstract class AbstractTestClient<T extends AbstractTestClient<T>> {
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;
    private final TestUserFactory testUserFactory;
    private final JwtService jwtService;

    private boolean runAsAdmin;

    protected AbstractTestClient(
            MockMvc mvc,
            ObjectMapper objectMapper,
            TestUserFactory testUserFactory,
            JwtService jwtService) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
        this.testUserFactory = testUserFactory;
        this.jwtService = jwtService;
        this.runAsAdmin = false;
    }

    @SuppressWarnings("unchecked")
    public T runAsAdmin() {
        this.runAsAdmin = true;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T runAsRegularUser() {
        this.runAsAdmin = false;
        return (T) this;
    }

    protected <C> ResultActions post(String url, C content) throws Exception {
        String json = objectMapper.writeValueAsString(content);
        return mvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header("Authorization", "Bearer " + getAuthToken())
                        .contentType(APPLICATION_JSON)
                        .content(json));
    }

    protected <C> ResultActions patch(String url, C content) throws Exception {
        String json = objectMapper.writeValueAsString(content);
        return mvc.perform(
                MockMvcRequestBuilders.patch(url)
                        .header("Authorization", "Bearer " + getAuthToken())
                        .contentType(APPLICATION_JSON)
                        .content(json));
    }

    protected <C> ResultActions put(String url, C content) throws Exception {
        String json = objectMapper.writeValueAsString(content);
        return mvc.perform(
                MockMvcRequestBuilders.put(url)
                        .header("Authorization", "Bearer " + getAuthToken())
                        .contentType(APPLICATION_JSON)
                        .content(json));
    }

    protected ResultActions get(String url) throws Exception {
        return mvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + getAuthToken()));
    }

    protected ResultActions delete(String url) throws Exception {
        return mvc.perform(
                MockMvcRequestBuilders.delete(url)
                        .header("Authorization", "Bearer " + getAuthToken()));
    }

    public <T> T parseResponse(ResultActions resultActions, Class<T> responseType) throws Exception {
        String content = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, responseType);
    }

    private String getAuthToken() {
        var userEntity = runAsAdmin
                ? testUserFactory.createAdmin("actingTestUser")
                : testUserFactory.createUser("actingTestUser");
        return jwtService.generateToken(User.builder().username(userEntity.getUsername()).build());
    }
}
