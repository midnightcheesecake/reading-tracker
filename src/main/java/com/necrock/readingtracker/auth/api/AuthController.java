package com.necrock.readingtracker.auth.api;

import com.necrock.readingtracker.auth.api.dto.LoginRequest;
import com.necrock.readingtracker.auth.api.dto.LoginResponse;
import com.necrock.readingtracker.auth.api.dto.RegisterRequest;
import com.necrock.readingtracker.auth.api.dto.RegisterResponse;
import com.necrock.readingtracker.auth.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthDtoMapper mapper;

    public AuthController(AuthService authService, AuthDtoMapper mapper) {
        this.authService = authService;
        this.mapper = mapper;
    }

    @PostMapping("/register")
    @ResponseStatus(CREATED)
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        return mapper.toRegisterResponse(authService.register(mapper.toDomainModel(request)));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return mapper.toLoginResponse(authService.login(mapper.toDomainModel(request)));
    }
}
