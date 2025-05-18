package com.necrock.readingtracker.user.api.self;

import com.necrock.readingtracker.testsupport.user.SelfUserTestClient;
import com.necrock.readingtracker.testsupport.user.TestUserFactory;
import com.necrock.readingtracker.user.api.self.dto.SelfUserDetailsDto;
import com.necrock.readingtracker.user.api.self.dto.UpdatePasswordRequest;
import com.necrock.readingtracker.user.api.self.dto.UpdateUserDetailsRequest;
import com.necrock.readingtracker.user.persistence.UserEntity;
import com.necrock.readingtracker.user.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static com.necrock.readingtracker.user.common.UserStatus.DELETED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import({SelfUserTestClient.Config.class, TestUserFactory.Config.class})
class SelfUserControllerTest {

    @Autowired
    SelfUserTestClient testClient;

    @Autowired
    TestUserFactory testUserFactory;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void getUser_returns200Ok() throws Exception {
        var testUser = testUserFactory.createUser("target-user");

        testClient.runAsUser(testUser).getUser()
                .andExpect(status().isOk());
    }

    @Test
    public void getUser_returnsUser() throws Exception {
        var testUser = testUserFactory.createUser("target-user");

        var result = testClient.runAsUser(testUser).getUser();

        var responseDto = testClient.parseResponse(result, SelfUserDetailsDto.class);
        assertThat(responseDto.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(responseDto.getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    public void updateUser_returns200Ok() throws Exception {
        var testUser = testUserFactory.createUser("target-user");
        var request = UpdateUserDetailsRequest.builder().email("new.email.address@email.com").build();

        testClient.runAsUser(testUser).updateUser(request)
                .andExpect(status().isOk());
    }

    @Test
    public void updateUser_changesUserDetails() throws Exception {
        var testUser = testUserFactory.createUser("target-user");
        var newEmail = "new.email.address@email.com";
        var request = UpdateUserDetailsRequest.builder().email(newEmail).build();

        testClient.runAsUser(testUser).updateUser(request);

        var updatedUser = getCurrentUser(testUser);
        assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
    }

    @Test
    public void setNewPassword_returns200Ok() throws Exception {
        var testUser = testUserFactory.createUser("target-user");
        var request = new UpdatePasswordRequest("newpassword");

        testClient.runAsUser(testUser).setNewPassword(request)
                .andExpect(status().isOk());
    }

    @Test
    public void setNewPassword_changesPassword() throws Exception {
        var testUser = testUserFactory.createUser("target-user");
        var newPassword = "newPassword";
        var request = new UpdatePasswordRequest(newPassword);

        testClient.runAsUser(testUser).setNewPassword(request);

        assertThat(passwordEncoder.matches(newPassword, getCurrentUser(testUser).getPasswordHash())).isTrue();
    }

    @Test
    public void deleteUser_returns204NoContent() throws Exception {
        var testUser = testUserFactory.createUser("target-user");

        testClient.runAsUser(testUser).deleteUser()
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteUser_changesUserStatus() throws Exception {
        var testUser = testUserFactory.createUser("target-user");

        testClient.runAsUser(testUser).deleteUser();

        assertThat(getCurrentUser(testUser).getStatus()).isEqualTo(DELETED);
    }

    @Test
    public void deleteUser_disablesUserToken() throws Exception {
        var testUser = testUserFactory.createUser("target-user");

        testClient.runAsUser(testUser).deleteUser();

        testClient.runAsUser(testUser).getUser()
                .andExpect(status().isForbidden());
    }

    private UserEntity getCurrentUser(UserEntity testUser) {
        return userRepository.findById(testUser.getId()).orElseThrow();
    }
}