package com.necrock.readingtracker.testsupport.auth;

import com.necrock.readingtracker.security.service.JwtService;
import com.necrock.readingtracker.user.persistence.UserEntity;
import com.necrock.readingtracker.user.persistence.UserRepository;
import com.necrock.readingtracker.user.service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.necrock.readingtracker.user.common.UserRole.USER;
import static com.necrock.readingtracker.user.common.UserStatus.ACTIVE;

@Component
public class TestAuthHelper {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public TestAuthHelper(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String createUserAndGetToken(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isEmpty()) {
            UserEntity user =
                    UserEntity.builder()
                            .setUsername(username)
                            .setPasswordHash(passwordEncoder.encode(rawPassword))
                            .setEmail("email")
                            .setRole(USER)
                            .setStatus(ACTIVE)
                            .build();
            userRepository.save(user);
        }
        return jwtService.generateToken(User.builder().username(username).build());
    }

    public String generateTokenFor(UserEntity testUser) {
        return jwtService.generateToken(User.builder().username(testUser.getUsername()).build());
    }
}
