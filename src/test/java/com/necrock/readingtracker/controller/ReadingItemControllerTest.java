package com.necrock.readingtracker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReadingItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createReadingItem_withValidInput_returns201Created() throws Exception {
        String json = """
            {
              "title": "Clean Architecture",
              "type": "BOOK",
              "author": "Robert C. Martin",
              "numberChapters": 30
            }
            """;

        mockMvc.perform(post("/api/items")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void getAllItems_returnsAllReadingItems() throws Exception {
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}