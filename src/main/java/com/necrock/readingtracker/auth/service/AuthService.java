package com.necrock.readingtracker.auth.service;

import com.necrock.readingtracker.auth.service.model.AuthResponse;
import com.necrock.readingtracker.auth.service.model.UserLogin;
import com.necrock.readingtracker.auth.service.model.UserRegistration;
import com.necrock.readingtracker.exception.UnauthorizedException;
import com.necrock.readingtracker.security.service.CustomUserDetails;
import com.necrock.readingtracker.security.service.JwtService;
import com.necrock.readingtracker.user.service.UserService;
import com.necrock.readingtracker.user.service.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authManager,
                       JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(UserRegistration request) {
        var newUser = userService.addUser(
                User.builder()
                        .username(request.username())
                        .passwordHash(passwordEncoder.encode(request.password()))
                        .email(request.email())
                        .build());
        return new AuthResponse(jwtService.generateToken(newUser));
    }

    public AuthResponse login(UserLogin request) {
        try {
            var auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));

            var userDetails = (CustomUserDetails) auth.getPrincipal();
            return new AuthResponse(jwtService.generateToken(userDetails.getUser()));
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Username or password incorrect");
        }
    }

}
