package com.necrock.readingtracker.auth.api;

import com.necrock.readingtracker.auth.api.dto.LoginRequest;
import com.necrock.readingtracker.auth.api.dto.RegisterRequest;
import com.necrock.readingtracker.auth.api.dto.RegisterResponse;
import com.necrock.readingtracker.security.service.JwtService;
import com.necrock.readingtracker.testsupport.auth.AuthTestClient;
import com.necrock.readingtracker.user.persistence.UserEntity;
import com.necrock.readingtracker.user.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.necrock.readingtracker.user.common.UserRole.USER;
import static com.necrock.readingtracker.user.common.UserStatus.ACTIVE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AuthTestClient.Config.class)
class AuthControllerTest {

    @Autowired
    AuthTestClient testClient;

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtService jwtService;

    @Test
    void register_returns201Created() throws Exception {
        var username = "user_register_returns201Created";
        var request = new RegisterRequest(username, "email@provider.com", "somePassword");

        testClient.register(request)
                .andExpect(status().isCreated());
    }

    @Test
    void register_returnsJwtToken() throws Exception {
        var username = "user_register_returnsJwtToken";
        var request = new RegisterRequest(username, "email@provider.com", "somePassword");

        var result = testClient.register(request);

        var response = testClient.parseResponse(result, RegisterResponse.class);
        assertThat(jwtService.getToken(response.token()).getUsername()).isEqualTo(username);
    }

    @Test
    void register_withExistingUsername_returns409Conflict() throws Exception {
        var username = "user_register_withExistingUsername_returns409Conflict";
        var userEntity = UserEntity.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode("somePassword"))
                .email("email")
                .role(USER)
                .status(ACTIVE)
                .build();
        userRepository.save(userEntity);
        var request = new RegisterRequest(username, "email@provider.com", "someOtherPassword");

        testClient.register(request)
                .andExpect(status().isConflict());
    }

    @Test
    void login_withValidCredentials_returns200Ok() throws Exception {
        var username = "user_login_withValidCredentials_returns200Ok";
        var password = "somePassword";
        var userEntity = UserEntity.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .email("email")
                .role(USER)
                .status(ACTIVE)
                .build();
        userRepository.save(userEntity);
        var login = new LoginRequest(username, password);

        testClient.login(login)
                .andExpect(status().isOk());
    }

    @Test
    void login_withValidCredentials_returnsJwtToken() throws Exception {
        var username = "user_login_withValidCredentials_returnsJwtToken";
        var password = "somePassword";
        var userEntity = UserEntity.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .email("email")
                .role(USER)
                .status(ACTIVE)
                .build();
        userRepository.save(userEntity);
        var login = new LoginRequest(username, password);

        var result = testClient.login(login);

        var response = testClient.parseResponse(result, RegisterResponse.class);
        assertThat(jwtService.getToken(response.token()).getUsername()).isEqualTo(username);
    }

    @Test
    void login_withWrongPassword_returns401Unauthorized() throws Exception {
        var username = "user_login_withWrongPassword_returns401Unauthorized";
        var password = "somePassword";
        var userEntity = UserEntity.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .email("email")
                .role(USER)
                .status(ACTIVE)
                .build();
        userRepository.save(userEntity);
        var login = new LoginRequest(username, "wrongPassword");

        testClient.login(login)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withUnknownUsername_returns401Unauthorized() throws Exception {
        var username = "user_login_withUnknownUsername_returns401Unauthorized";
        var login = new LoginRequest(username, "somePassword");

        testClient.login(login)
                .andExpect(status().isUnauthorized());
    }
}