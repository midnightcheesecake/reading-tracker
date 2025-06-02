package com.necrock.readingtracker.testsupport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.necrock.readingtracker.security.service.JwtService;
import com.necrock.readingtracker.testsupport.user.TestUserFactory;
import com.necrock.readingtracker.user.persistence.UserEntity;
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

    private UserEntity runningUser;

    protected AbstractTestClient(
            MockMvc mvc,
            ObjectMapper objectMapper,
            TestUserFactory testUserFactory,
            JwtService jwtService) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
        this.testUserFactory = testUserFactory;
        this.jwtService = jwtService;
        this.runningUser = testUserFactory.createUser("actingTestUser");
    }

    @SuppressWarnings("unchecked")
    public T runAsAdmin() {
        this.runningUser = testUserFactory.createAdmin("actingTestUser");
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T runAsRegularUser() {
        this.runningUser = testUserFactory.createUser("actingTestUser");
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T runAsUser(UserEntity user) {
        this.runningUser = user;
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

    public <R> R parseResponse(ResultActions resultActions, Class<R> responseType) throws Exception {
        String content = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, responseType);
    }

    public <R> R parseResponse(ResultActions resultActions, TypeReference<R> responseType) throws Exception {
        String content = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, responseType);
    }

    private String getAuthToken() {
        return jwtService.generateToken(User.builder().username(runningUser.getUsername()).build());
    }
}
