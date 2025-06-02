package com.necrock.readingtracker.readingprogress.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.necrock.readingtracker.readingprogress.api.dto.CreateReadingProgressRequest;
import com.necrock.readingtracker.readingprogress.api.dto.ReadingProgressDetailsDto;
import com.necrock.readingtracker.readingprogress.api.dto.UpdateReadingProgressRequest;
import com.necrock.readingtracker.testsupport.readingProgress.ReadingProgressTestClient;
import com.necrock.readingtracker.testsupport.readingProgress.TestReadingProgressFactory;
import com.necrock.readingtracker.testsupport.readingitem.TestReadingItemFactory;
import com.necrock.readingtracker.testsupport.user.TestUserFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import({
        ReadingProgressTestClient.Config.class,
        TestReadingProgressFactory.Config.class,
        TestUserFactory.Config.class,
        TestReadingItemFactory.Config.class})
class ReadingProgressControllerTest {

    @Autowired
    ReadingProgressTestClient testClient;

    @Autowired
    TestReadingProgressFactory testReadingProgressFactory;

    @Autowired
    TestUserFactory testUserFactory;
    @Autowired
    TestReadingItemFactory testReadingItemFactory;

    @Test
    void createReadingProgress_returns201Created() throws Exception {
        var testReadingItem = testReadingItemFactory.createReadingItem("book", "author");
        var request = CreateReadingProgressRequest.builder()
                .readingItemId(testReadingItem.getId())
                .lastReadChapter(10)
                .build();

        testClient.runAsRegularUser()
                .addReadingProgress(request)
                .andExpect(status().isCreated());
    }

    @Test
    void createReadingProgress_returnsNewReadingProgress() throws Exception {
        var testReadingItem = testReadingItemFactory.createReadingItem("book", "author");
        var request = CreateReadingProgressRequest.builder()
                .readingItemId(testReadingItem.getId())
                .lastReadChapter(10)
                .build();

        ResultActions result = testClient.runAsRegularUser()
                .addReadingProgress(request);

        var responseDto = testClient.parseResponse(result, ReadingProgressDetailsDto.class);
        assertThat(responseDto.getReadingItem().getId()).isEqualTo(testReadingItem.getId());
        assertThat(responseDto.getReadingItem().getTitle()).isEqualTo(testReadingItem.getTitle());
        assertThat(responseDto.getReadingItem().getAuthor()).isEqualTo(testReadingItem.getAuthor());
        assertThat(responseDto.getReadingItem().getTotalChapters()).isEqualTo(testReadingItem.getTotalChapters());
        assertThat(responseDto.getLastReadChapter()).isEqualTo(request.getLastReadChapter());
    }

    @Test
    void createReadingProgress_withMissingReadingItemId_returns400BadRequest() throws Exception {
        var request = CreateReadingProgressRequest.builder()
                .lastReadChapter(10)
                .build();

        testClient.runAsRegularUser()
                .addReadingProgress(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReadingProgress_withUnknownReadingItemId_returns404NotFound() throws Exception {
        var request = CreateReadingProgressRequest.builder()
                .readingItemId(42L)
                .lastReadChapter(10)
                .build();

        testClient.runAsRegularUser()
                .addReadingProgress(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void createReadingProgress_withExistingProgressForItem_returns409Conflict() throws Exception {
        var testReadingItem = testReadingItemFactory.createReadingItem("book", "author");
        var request = CreateReadingProgressRequest.builder()
                .readingItemId(testReadingItem.getId())
                .lastReadChapter(10)
                .build();
        testClient.runAsRegularUser().addReadingProgress(request);

        testClient.runAsRegularUser()
                .addReadingProgress(request)
                .andExpect(status().isConflict());
    }

    @Test
    void createReadingProgress_withNegativeLastChapterRead_returns400BadRequest() throws Exception {
        var testReadingItem = testReadingItemFactory.createReadingItem("book", "author");
        var request = CreateReadingProgressRequest.builder()
                .readingItemId(testReadingItem.getId())
                .lastReadChapter(-10)
                .build();

        testClient.runAsRegularUser()
                .addReadingProgress(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReadingProgress_returns200Ok() throws Exception {
        var testUser = testUserFactory.createUser("user");
        var testReadingItem = testReadingItemFactory.createReadingItem("book", "author");
        testReadingProgressFactory.createReadingProgress(testUser, testReadingItem);

        testClient.runAsUser(testUser)
                .getReadingProgress(testReadingItem.getId())
                .andExpect(status().isOk());
    }

    @Test
    void getReadingProgress_returnsReadingProgress() throws Exception {
        var testUser = testUserFactory.createUser("user");
        var testReadingItem = testReadingItemFactory.createReadingItem("book", "author");
        var testReadingProgress = testReadingProgressFactory.createReadingProgress(testUser, testReadingItem);

        ResultActions result = testClient.runAsUser(testUser)
                .getReadingProgress(testReadingItem.getId());

        var responseDto = testClient.parseResponse(result, ReadingProgressDetailsDto.class);
        assertThat(responseDto.getReadingItem().getId()).isEqualTo(testReadingItem.getId());
        assertThat(responseDto.getReadingItem().getTitle()).isEqualTo(testReadingItem.getTitle());
        assertThat(responseDto.getReadingItem().getAuthor()).isEqualTo(testReadingItem.getAuthor());
        assertThat(responseDto.getReadingItem().getTotalChapters()).isEqualTo(testReadingItem.getTotalChapters());
        assertThat(responseDto.getLastReadChapter()).isEqualTo(testReadingProgress.getLastReadChapter());
    }

    @Test
    void getReadingProgress_withUnknownReadingItemId_returns404NotFound() throws Exception {
        testClient.runAsRegularUser()
                .getReadingProgress(666L)
                .andExpect(status().isNotFound());
    }

    @Test
    void getReadingProgress_withUnknownReadingProgress_returns404NotFound() throws Exception {
        var testReadingItem = testReadingItemFactory.createReadingItem("book", "author");

        testClient.runAsRegularUser()
                .getReadingProgress(testReadingItem.getId())
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllReadingProgress() throws Exception {
        var testUser = testUserFactory.createUser("user");
        var testReadingItem1 = testReadingItemFactory.createReadingItem("book", "author");
        var testReadingProgress1 = testReadingProgressFactory.createReadingProgress(testUser, testReadingItem1);
        var testReadingItem2 = testReadingItemFactory.createReadingItem("book", "author");
        var testReadingProgress2 = testReadingProgressFactory.createReadingProgress(testUser, testReadingItem2);

        ResultActions result = testClient.runAsUser(testUser)
                .listReadingProgress();

        var responseDto = testClient.parseResponse(result, new TypeReference<List<ReadingProgressDetailsDto>>() {});
        assertThat(responseDto).hasSize(2);
        assertThat(responseDto).anySatisfy(progressDto -> {
            assertThat(progressDto.getReadingItem().getId()).isEqualTo(testReadingItem1.getId());
            assertThat(progressDto.getLastReadChapter()).isEqualTo(testReadingProgress1.getLastReadChapter());
        });
        assertThat(responseDto).anySatisfy(progressDto -> {
            assertThat(progressDto.getReadingItem().getId()).isEqualTo(testReadingItem2.getId());
            assertThat(progressDto.getLastReadChapter()).isEqualTo(testReadingProgress2.getLastReadChapter());
        });
    }

    @Test
    void updateReadingProgress_returns200Ok() throws Exception {
        var testUser = testUserFactory.createUser("user");
        var testReadingItem = testReadingItemFactory.createReadingItem("book", "author");
        testReadingProgressFactory.createReadingProgress(testUser, testReadingItem);
        UpdateReadingProgressRequest request = UpdateReadingProgressRequest.builder().lastChapterRead(15).build();

        testClient.runAsUser(testUser)
                .updateReadingProgress(testReadingItem.getId(), request)
                .andExpect(status().isOk());
    }

    @Test
    void updateReadingProgress_returnsUpdatedReadingProgress() throws Exception {
        var testUser = testUserFactory.createUser("user");
        var testReadingItem = testReadingItemFactory.createReadingItem("book", "author");
        testReadingProgressFactory.createReadingProgress(testUser, testReadingItem);
        UpdateReadingProgressRequest request = UpdateReadingProgressRequest.builder().lastChapterRead(15).build();

        var result = testClient.runAsUser(testUser)
                .updateReadingProgress(testReadingItem.getId(), request)
                .andExpect(status().isOk());

        var responseDto = testClient.parseResponse(result, ReadingProgressDetailsDto.class);
        assertThat(responseDto.getReadingItem().getId()).isEqualTo(testReadingItem.getId());
        assertThat(responseDto.getLastReadChapter()).isEqualTo(request.getLastReadChapter());
    }

    @Test
    void updateReadingProgress_withUnknownReadingItemId_returns404NotFound() throws Exception {
        UpdateReadingProgressRequest request = UpdateReadingProgressRequest.builder().lastChapterRead(15).build();

        testClient.runAsRegularUser()
                .updateReadingProgress(666L, request)
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReadingProgress_withUnknownReadingProgress_returns404NotFound() throws Exception {
        var testReadingItem = testReadingItemFactory.createReadingItem("book", "author");
        UpdateReadingProgressRequest request = UpdateReadingProgressRequest.builder().lastChapterRead(15).build();

        testClient.runAsRegularUser()
                .updateReadingProgress(testReadingItem.getId(), request)
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReadingProgress_withNegativeLastChapterRead_returns400BadRequest() throws Exception {
        var testUser = testUserFactory.createUser("user");
        var testReadingItem = testReadingItemFactory.createReadingItem("book", "author");
        testReadingProgressFactory.createReadingProgress(testUser, testReadingItem);
        UpdateReadingProgressRequest request = UpdateReadingProgressRequest.builder().lastChapterRead(-15).build();

        testClient.runAsUser(testUser)
                .updateReadingProgress(testReadingItem.getId(), request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteReadingProgress_returns204NoContent() throws Exception {
        var testUser = testUserFactory.createUser("user");
        var testReadingItem = testReadingItemFactory.createReadingItem("book", "author");
        testReadingProgressFactory.createReadingProgress(testUser, testReadingItem);

        testClient.runAsUser(testUser)
                .deleteReadingProgress(testReadingItem.getId())
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReadingProgress_withUnknownReadingItemId_returns404NotFound() throws Exception {
        testClient.runAsRegularUser()
                .deleteReadingProgress(666L)
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteReadingProgress_withUnknownReadingProgress_returns404NotFound() throws Exception {
        var testReadingItem = testReadingItemFactory.createReadingItem("book", "author");

        testClient.runAsRegularUser()
                .deleteReadingProgress(testReadingItem.getId())
                .andExpect(status().isNotFound());
    }
}