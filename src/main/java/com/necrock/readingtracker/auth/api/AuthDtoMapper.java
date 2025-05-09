package com.necrock.readingtracker.auth.api;

import com.necrock.readingtracker.auth.api.dto.LoginRequest;
import com.necrock.readingtracker.auth.api.dto.LoginResponse;
import com.necrock.readingtracker.auth.api.dto.RegisterRequest;
import com.necrock.readingtracker.auth.api.dto.RegisterResponse;
import com.necrock.readingtracker.auth.service.model.AuthResponse;
import com.necrock.readingtracker.auth.service.model.UserLogin;
import com.necrock.readingtracker.auth.service.model.UserRegistration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthDtoMapper {

    RegisterResponse toRegisterResponse(AuthResponse response);

    UserRegistration toDomainModel(RegisterRequest request);

    LoginResponse toLoginResponse(AuthResponse response);

    UserLogin toDomainModel(LoginRequest request);
}
