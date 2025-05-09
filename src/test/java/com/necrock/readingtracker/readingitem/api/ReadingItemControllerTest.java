package com.necrock.readingtracker.readingitem.api;

import com.necrock.readingtracker.readingitem.api.dto.CreateReadingItemDto;
import com.necrock.readingtracker.readingitem.api.dto.ReadingItemDetailsDto;
import com.necrock.readingtracker.readingitem.api.dto.UpdateReadingItemDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.ResultActions;

import static com.necrock.readingtracker.readingitem.common.ReadingItemType.BOOK;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(ReadingItemTestClientConfig.class)
class ReadingItemControllerTest {

    @Autowired
    ReadingItemTestClient testClient;

    @Test
    void createReadingItem_returns201Created() throws Exception {
        var createReadingItem = CreateReadingItemDto.builder()
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .type(BOOK)
                .numberChapters(30)
                .build();

        testClient.addReadingItem(createReadingItem)
                .andExpect(status().isCreated());
    }

    @Test
    void createReadingItem_returnsNewReadingItem() throws Exception {
        var createReadingItem = CreateReadingItemDto.builder()
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .type(BOOK)
                .numberChapters(30)
                .build();

        ResultActions result = testClient.addReadingItem(createReadingItem);

        var responseDto = testClient.parseResponse(result, ReadingItemDetailsDto.class);
        assertThat(responseDto.getId()).isGreaterThan(0);
        assertThat(responseDto.getTitle()).isEqualTo(createReadingItem.getTitle());
        assertThat(responseDto.getAuthor()).isEqualTo(createReadingItem.getAuthor());
        assertThat(responseDto.getType()).isEqualTo(createReadingItem.getType());
        assertThat(responseDto.getNumberChapters()).isEqualTo(createReadingItem.getNumberChapters());
    }

    @Test
    void createReadingItem_withMissingTitle_returns400BadRequest() throws Exception {
        var createReadingItem = CreateReadingItemDto.builder()
                .author("Robert C. Martin")
                .type(BOOK)
                .numberChapters(30)
                .build();

        testClient.addReadingItem(createReadingItem)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.title").value("Title is required"));
    }

    @Test
    void createReadingItem_withMissingType_returns400BadRequest() throws Exception {
        var createReadingItem = CreateReadingItemDto.builder()
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .numberChapters(30)
                .build();

        testClient.addReadingItem(createReadingItem)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.type").value("Type is required"));
    }

    @Test
    void createReadingItem_withMissingAuthor_returns400BadRequest() throws Exception {
        var createReadingItem = CreateReadingItemDto.builder()
                .title("Clean Architecture")
                .type(BOOK)
                .numberChapters(30)
                .build();

        testClient.addReadingItem(createReadingItem)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.author").value("Author is required"));
    }

    @Test
    void createReadingItem_withNegativeNumberChapters_returns400BadRequest() throws Exception {
        var createReadingItem = CreateReadingItemDto.builder()
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .type(BOOK)
                .numberChapters(-30)
                .build();

        testClient.addReadingItem(createReadingItem)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.numberChapters").value("Number of chapters can not be negative"));
    }

    @Test
    void updateReadingItem_returns200Ok() throws Exception {
        var createReadingItem = CreateReadingItemDto.builder()
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .type(BOOK)
                .numberChapters(30)
                .build();
        var updateReadingItem = UpdateReadingItemDto.builder()
                .numberChapters(40)
                .build();

        var createResult = testClient.addReadingItem(createReadingItem);
        var createResponseDto = testClient.parseResponse(createResult, ReadingItemDetailsDto.class);
        long id = createResponseDto.getId();

        testClient.updateReadingItem(id, updateReadingItem)
                .andExpect(status().isOk());
    }

    @Test
    void updateReadingItem_returnsUpdatedReadingItem() throws Exception {
        var createReadingItem = CreateReadingItemDto.builder()
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .type(BOOK)
                .numberChapters(30)
                .build();
        var updateReadingItem = UpdateReadingItemDto.builder()
                .numberChapters(40)
                .build();

        var createResult = testClient.addReadingItem(createReadingItem);
        var createResponseDto = testClient.parseResponse(createResult, ReadingItemDetailsDto.class);
        long id = createResponseDto.getId();

        var result = testClient.updateReadingItem(id, updateReadingItem);

        var responseDto = testClient.parseResponse(result, ReadingItemDetailsDto.class);
        assertThat(responseDto.getId()).isEqualTo(id);
        assertThat(responseDto.getTitle()).isEqualTo(createReadingItem.getTitle());
        assertThat(responseDto.getAuthor()).isEqualTo(createReadingItem.getAuthor());
        assertThat(responseDto.getType()).isEqualTo(createReadingItem.getType());
        assertThat(responseDto.getNumberChapters()).isEqualTo(updateReadingItem.getNumberChapters());
    }

    @Test
    void updateReadingItem_withUnknownId_returns404NotFound() throws Exception {
        var updateReadingItem = UpdateReadingItemDto.builder()
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .type(BOOK)
                .numberChapters(30)
                .build();

        testClient.updateReadingItem(98765, updateReadingItem)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND_ERROR"))
                .andExpect(jsonPath("$.message").value("No reading item with id 98765"));
    }

    @Test
    void updateReadingItem_withNegativeNumberChapters_returns400BadRequest() throws Exception {
        var createReadingItem = CreateReadingItemDto.builder()
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .type(BOOK)
                .numberChapters(30)
                .build();
        var updateReadingItem = UpdateReadingItemDto.builder()
                .numberChapters(-30)
                .build();

        var createResult = testClient.addReadingItem(createReadingItem);
        var createResponseDto = testClient.parseResponse(createResult, ReadingItemDetailsDto.class);
        long id = createResponseDto.getId();

        testClient.updateReadingItem(id, updateReadingItem)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.numberChapters").value("Number of chapters can not be negative"));
    }

    @Test
    void deleteReadingItem_returns204NoContent() throws Exception {
        var createReadingItem = CreateReadingItemDto.builder()
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .type(BOOK)
                .numberChapters(30)
                .build();

        var createResult = testClient.addReadingItem(createReadingItem);
        var createResponseDto = testClient.parseResponse(createResult, ReadingItemDetailsDto.class);
        long id = createResponseDto.getId();

        testClient.deleteReadingItem(id)
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReadingItem_withUnknownId_returns404NotFound() throws Exception {
        testClient.deleteReadingItem(98765)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND_ERROR"))
                .andExpect(jsonPath("$.message").value("No reading item with id 98765"));
    }

    @Test
    void getReadingItem_returns200Ok() throws Exception {
        var createReadingItem = CreateReadingItemDto.builder()
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .type(BOOK)
                .numberChapters(30)
                .build();

        var createResult = testClient.addReadingItem(createReadingItem);
        var createResponseDto = testClient.parseResponse(createResult, ReadingItemDetailsDto.class);
        long id = createResponseDto.getId();

        testClient.getReadingItem(id)
                .andExpect(status().isOk());
    }

    @Test
    void getReadingItem_returnsReadingItem() throws Exception {
        var createReadingItem = CreateReadingItemDto.builder()
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .type(BOOK)
                .numberChapters(30)
                .build();

        var createResult = testClient.addReadingItem(createReadingItem);
        var createResponseDto = testClient.parseResponse(createResult, ReadingItemDetailsDto.class);
        long id = createResponseDto.getId();

        var result = testClient.getReadingItem(id);

        var responseDto = testClient.parseResponse(result, ReadingItemDetailsDto.class);
        assertThat(responseDto.getId()).isEqualTo(id);
        assertThat(responseDto.getTitle()).isEqualTo(createReadingItem.getTitle());
        assertThat(responseDto.getAuthor()).isEqualTo(createReadingItem.getAuthor());
        assertThat(responseDto.getType()).isEqualTo(createReadingItem.getType());
        assertThat(responseDto.getNumberChapters()).isEqualTo(createReadingItem.getNumberChapters());
    }

    @Test
    void getReadingItem_withUnknownId_returns404NotFound() throws Exception {
        testClient.getReadingItem(98765)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND_ERROR"))
                .andExpect(jsonPath("$.message").value("No reading item with id 98765"));
    }

    @Test
    void getReadingItem_withDeletedReadingItemId_returns404NotFound() throws Exception {
        var createReadingItem = CreateReadingItemDto.builder()
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .type(BOOK)
                .numberChapters(30)
                .build();

        var createResult = testClient.addReadingItem(createReadingItem);
        var createResponseDto = testClient.parseResponse(createResult, ReadingItemDetailsDto.class);
        long id = createResponseDto.getId();
        testClient.deleteReadingItem(id).andReturn();

        testClient.getReadingItem(id)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND_ERROR"))
                .andExpect(jsonPath("$.message").value("No reading item with id " + id));
    }

    @Test
    void getAllItems_returnsAllReadingItems() throws Exception {
        testClient.listReadingItems()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
