package com.necrock.readingtracker.user.api.admin;

import com.necrock.readingtracker.testsupport.user.AdminUserTestClient;
import com.necrock.readingtracker.testsupport.user.TestUserFactory;
import com.necrock.readingtracker.user.api.admin.dto.AdminUserDetailsDto;
import com.necrock.readingtracker.user.api.admin.dto.UpdateUserRoleRequest;
import com.necrock.readingtracker.user.api.admin.dto.UpdateUserStatusRequest;
import com.necrock.readingtracker.user.common.UserRole;
import com.necrock.readingtracker.user.common.UserStatus;
import com.necrock.readingtracker.user.persistence.UserEntity;
import com.necrock.readingtracker.user.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import({AdminUserTestClient.Config.class, TestUserFactory.Config.class})
class AdminUserControllerTest {

    @Autowired
    AdminUserTestClient testClient;

    @Autowired
    TestUserFactory testUserFactory;
    @Autowired
    UserRepository userRepository;

    @Test
    public void getUser_returns200Ok() throws Exception {
        var testUser = testUserFactory.createUser("target-user");

        testClient.runAsAdmin().getUser(testUser.getId())
                .andExpect(status().isOk());
    }

    @Test
    public void getUser_returnsUser() throws Exception {
        var testUser = testUserFactory.createUser("target-user");

        var result = testClient.runAsAdmin().getUser(testUser.getId());

        var responseDto = testClient.parseResponse(result, AdminUserDetailsDto.class);
        assertThat(responseDto.getId()).isEqualTo(testUser.getId());
        assertThat(responseDto.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(responseDto.getRole()).isEqualTo(testUser.getRole());
        assertThat(responseDto.getStatus()).isEqualTo(testUser.getStatus());
    }

    @Test
    public void getUser_withUnknownId_returns404NotFound() throws Exception {
        testClient.runAsAdmin().getUser(98765)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND_ERROR"))
                .andExpect(jsonPath("$.message").value("No user with id 98765"));
    }

    @Test
    public void setUserStatus_returns200Ok() throws Exception {
        var testUser = testUserFactory.createUser("target-user");
        var request = new UpdateUserStatusRequest(UserStatus.DELETED);

        testClient.runAsAdmin().setUserStatus(testUser.getId(), request)
                .andExpect(status().isOk());
    }

    @Test
    public void setUserStatus_changesUserStatus() throws Exception {
        var testUser = testUserFactory.createUser("target-user");
        var request = new UpdateUserStatusRequest(UserStatus.DELETED);

        testClient.runAsAdmin().setUserStatus(testUser.getId(), request).andReturn();

        assertThat(getCurrentUser(testUser).getStatus()).isEqualTo(UserStatus.DELETED);
    }

    @Test
    public void setUserStatus_withUnknownId_returns404NotFound() throws Exception {
        var request = new UpdateUserStatusRequest(UserStatus.DELETED);

        testClient.runAsAdmin().setUserStatus(98765, request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND_ERROR"))
                .andExpect(jsonPath("$.message").value("No user with id 98765"));
    }

    @Test
    public void setUserRole_returns200Ok() throws Exception {
        var testUser = testUserFactory.createUser("target-user");
        var request = new UpdateUserRoleRequest(UserRole.ADMIN);

        testClient.runAsAdmin().setUserRole(testUser.getId(), request)
                .andExpect(status().isOk());
    }

    @Test
    public void setUserRole_changesUserRole() throws Exception {
        var testUser = testUserFactory.createUser("target-user");
        var request = new UpdateUserRoleRequest(UserRole.ADMIN);

        testClient.runAsAdmin().setUserRole(testUser.getId(), request).andReturn();

        assertThat(getCurrentUser(testUser).getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    public void setUserRole_withUnknownId_returns404NotFound() throws Exception {
        var request = new UpdateUserRoleRequest(UserRole.ADMIN);

        testClient.runAsAdmin().setUserRole(98765, request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND_ERROR"))
                .andExpect(jsonPath("$.message").value("No user with id 98765"));
    }

    private UserEntity getCurrentUser(UserEntity testUser) {
        return userRepository.findById(testUser.getId()).orElseThrow();
    }
}