package com.necrock.readingtracker.readingitem.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.necrock.readingtracker.auth.TestAuthHelper;
import com.necrock.readingtracker.security.service.JwtService;
import com.necrock.readingtracker.user.service.model.User;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReadingItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestAuthHelper authHelper;

    @Test
    void createReadingItem_returns201Created() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");
        String json = """
                {
                  "title": "Clean Architecture",
                  "type": "BOOK",
                  "author": "Robert C. Martin",
                  "numberChapters": 30
                }
                """;

        mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void createReadingItem_returnsNewReadingItem() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");
        String json = """
                {
                  "title": "Clean Architecture",
                  "type": "BOOK",
                  "author": "Robert C. Martin",
                  "numberChapters": 30
                }
                """;

        mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.id").value(greaterThan(0)))
                .andExpect(jsonPath("$.title").value("Clean Architecture"))
                .andExpect(jsonPath("$.type").value("BOOK"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.numberChapters").value(30));
    }

    @Test
    void createReadingItem_withMissingTitle_returns400BadRequest() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");
        String json = """
                {
                  "type": "BOOK",
                  "author": "Robert C. Martin",
                  "numberChapters": 30
                }
                """;

        mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.title").value("Title is required"));
    }

    @Test
    void createReadingItem_withMissingType_returns400BadRequest() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");
        String json = """
                {
                  "title": "Clean Architecture",
                  "author": "Robert C. Martin",
                  "numberChapters": 30
                }
                """;

        var result = mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.type").value("Type is required"));
    }

    @Test
    void createReadingItem_withMissingAuthor_returns400BadRequest() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");
        String json = """
                {
                  "title": "Clean Architecture",
                  "type": "BOOK",
                  "numberChapters": 30
                }
                """;

        mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.author").value("Author is required"));
    }

    @Test
    void createReadingItem_withNegativeNumberChapters_returns400BadRequest() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");
        String json = """
                {
                  "title": "Clean Architecture",
                  "type": "BOOK",
                  "author": "Robert C. Martin",
                  "numberChapters": -30
                }
                """;

        mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.numberChapters").value("Number of chapters can not be negative"));
    }

    @Test
    void updateReadingItem_returns200Ok() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");
        String createJson = """
                {
                  "title": "Clean Architecture",
                  "type": "BOOK",
                  "author": "Robert C. Martin",
                  "numberChapters": 30
                }
                """;
        String updateJson = """
                {
                  "numberChapters": 40
                }
                """;

        var createResult = mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(createJson))
                .andReturn();
        var id = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(patch("/api/items/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());
    }

    @Test
    void updateReadingItem_returnsUpdatedReadingItem() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");
        String createJson = """
                {
                  "title": "Clean Architecture",
                  "type": "BOOK",
                  "author": "Robert C. Martin",
                  "numberChapters": 30
                }
                """;
        String updateJson = """
                {
                  "numberChapters": 40
                }
                """;
        var createResult = mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(createJson))
                .andReturn();
        var id = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(patch("/api/items/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Clean Architecture"))
                .andExpect(jsonPath("$.type").value("BOOK"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.numberChapters").value(40));
    }

    @Test
    void updateReadingItem_withUnknownId_returns404NotFound() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");
        String json = """
                {
                  "title": "Clean Architecture",
                  "type": "BOOK",
                  "author": "Robert C. Martin",
                  "numberChapters": 30
                }
                """;

        mockMvc.perform(patch("/api/items/98765")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND_ERROR"))
                .andExpect(jsonPath("$.message").value("No reading item with id 98765"));
    }

    @Test
    void updateReadingItem_withNegativeNumberChapters_returns400BadRequest() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");
        String json = """
                {
                  "numberChapters": -30
                }
                """;

        mockMvc.perform(patch("/api/items/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.numberChapters").value("Number of chapters can not be negative"));
    }

    @Test
    void deleteReadingItem_returns204NoContent() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");
        String createJson = """
                {
                  "title": "Clean Architecture",
                  "type": "BOOK",
                  "author": "Robert C. Martin",
                  "numberChapters": 30
                }
                """;

        var createResult = mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(createJson))
                .andReturn();
        var id = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(delete("/api/items/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReadingItem_withUnknownId_returns404NotFound() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");

        mockMvc.perform(delete("/api/items/98765")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND_ERROR"))
                .andExpect(jsonPath("$.message").value("No reading item with id 98765"));
    }

    @Test
    void getReadingItem_returns200Ok() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");
        String createJson = """
                {
                  "title": "Clean Architecture",
                  "type": "BOOK",
                  "author": "Robert C. Martin",
                  "numberChapters": 30
                }
                """;
        var createResult = mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(createJson))
                .andReturn();
        var id = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/items/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void getReadingItem_returnsReadingItem() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");
        String createJson = """
                {
                  "title": "Clean Architecture",
                  "type": "BOOK",
                  "author": "Robert C. Martin",
                  "numberChapters": 30
                }
                """;
        var createResult = mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(createJson))
                .andReturn();
        var id = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/items/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Clean Architecture"))
                .andExpect(jsonPath("$.type").value("BOOK"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.numberChapters").value(30));
    }

    @Test
    void getReadingItem_withUnknownId_returns404NotFound() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");
        String json = """
                {
                  "title": "Clean Architecture",
                  "type": "BOOK",
                  "author": "Robert C. Martin",
                  "numberChapters": 30
                }
                """;

        mockMvc.perform(get("/api/items/98765")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND_ERROR"))
                .andExpect(jsonPath("$.message").value("No reading item with id 98765"));
    }

    @Test
    void getReadingItem_withDeletedReadingItemId_returns404NotFound() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");
        String createJson = """
                {
                  "title": "Clean Architecture",
                  "type": "BOOK",
                  "author": "Robert C. Martin",
                  "numberChapters": 30
                }
                """;
        var createResult = mockMvc.perform(post("/api/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(createJson))
                .andReturn();
        var id = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");
        mockMvc.perform(delete("/api/items/" + id)
                .header("Authorization", "Bearer " + token));

        mockMvc.perform(get("/api/items/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND_ERROR"))
                .andExpect(jsonPath("$.message").value("No reading item with id " + id));
    }

    @Test
    void getAllItems_returnsAllReadingItems() throws Exception {
        String token = authHelper.createUserAndGetToken("testUser", "password");

        mockMvc.perform(get("/api/items")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}