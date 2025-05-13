package com.necrock.readingtracker.auth.service;

import com.necrock.readingtracker.auth.service.model.UserLogin;
import com.necrock.readingtracker.auth.service.model.UserRegistration;
import com.necrock.readingtracker.testsupport.configuration.TestTimeConfig;
import com.necrock.readingtracker.exception.AlreadyExistsException;
import com.necrock.readingtracker.exception.UnauthorizedException;
import com.necrock.readingtracker.security.service.JwtService;
import com.necrock.readingtracker.user.persistence.UserEntity;
import com.necrock.readingtracker.user.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.function.Consumer;

import static com.necrock.readingtracker.user.common.UserRole.USER;
import static com.necrock.readingtracker.user.common.UserStatus.ACTIVE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import(TestTimeConfig.class)
@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService service;

    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @Test
    void registerUser_savesUser() {
        UserRegistration registration =
                new UserRegistration("username", "password", "email@provider.com");

        when(userRepository.save(any(UserEntity.class))).thenReturn(UserEntity.builder()
                .setId(42L)
                .setUsername(registration.username())
                .setPasswordHash(passwordEncoder.encode(registration.password()))
                .setEmail(registration.email())
                .setRole(USER)
                .setStatus(ACTIVE)
                .setCreatedAt(TestTimeConfig.NOW)
                .build());

        service.register(registration);

        var captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity savedUserEntity = captor.getValue();
        assertThat(savedUserEntity.getId()).isNull();
        assertThat(savedUserEntity.getUsername()).isEqualTo(registration.username());
        assertThat(passwordEncoder.matches(registration.password(), savedUserEntity.getPasswordHash())).isTrue();
        assertThat(savedUserEntity.getEmail()).isEqualTo(registration.email());
        assertThat(savedUserEntity.getRole()).isEqualTo(USER);
        assertThat(savedUserEntity.getStatus()).isEqualTo(ACTIVE);
        assertThat(savedUserEntity.getCreatedAt()).isEqualTo(TestTimeConfig.NOW);
    }

    @Test
    void registerUser_returnsValidJwtToken() {
        String username = "username";
        String password = "password";
        String email = "email@provider.com";
        UserRegistration registration =
                new UserRegistration(username, password, email);
        UserEntity savedUserEntity = UserEntity.builder()
                .setId(42L)
                .setUsername(username)
                .setPasswordHash(passwordEncoder.encode(password))
                .setEmail(email)
                .setRole(USER)
                .setStatus(ACTIVE)
                .setCreatedAt(TestTimeConfig.NOW)
                .build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        var result = service.register(registration);

        JwtService.Token token = jwtService.getToken(result.token());
        assertThat(token.getUsername()).isEqualTo(username);
    }

    @Test
    void registerUser_withExistingUsername_throwsAlreadyFound() {
        String username = "username";
        UserRegistration registration =
                new UserRegistration(username, "password", "email@provider.com");

        when(userRepository.save(any(UserEntity.class))).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.register(registration))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage("User with username '" + username + "' already exists");
    }

    @Test
    void login_findsUser() {
        var username = "user";
        var password = "password";
        var login = new UserLogin(username, password);
        var userEntity =
                testUserEntity(u -> u
                        .setUsername(username)
                        .setPasswordHash(passwordEncoder.encode(password)));

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(userEntity));

        service.login(login);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).findByUsername(captor.capture());
        assertThat(captor.getValue()).isEqualTo(username);
    }

    @Test
    void login_returnsValidJwtToken() {
        var username = "user";
        var password = "password";
        var login = new UserLogin(username, password);
        var userEntity =
                testUserEntity(u -> u
                        .setUsername(username)
                        .setPasswordHash(passwordEncoder.encode(password)));

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(userEntity));

        var result = service.login(login);

        JwtService.Token token = jwtService.getToken(result.token());
        assertThat(token.getUsername()).isEqualTo(username);
    }

    @Test
    void login_withIncorrectPassword_throwsUnauthorizedException() {
        var username = "user";
        var login = new UserLogin(username, "wrongPassword");
        var userEntity =
                testUserEntity(u -> u
                        .setUsername(username)
                        .setPasswordHash(passwordEncoder.encode("correctPassword")));

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(userEntity));

        assertThatThrownBy(() -> service.login(login))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void login_withUnknownUsername_throwsUnauthorizedException() {
        var username = "user";
        var login = new UserLogin(username, "password");

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(login))
                .isInstanceOf(UnauthorizedException.class);
    }

    private static UserEntity testUserEntity(Consumer<UserEntity.Builder> overrides) {
        var builder = UserEntity.builder()
                .setId(666L)
                .setUsername("username")
                .setEmail("email@provider.com")
                .setPasswordHash("#hash")
                .setStatus(ACTIVE)
                .setRole(USER);
        overrides.accept(builder);
        return builder.build();
    }
}
