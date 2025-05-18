package com.necrock.readingtracker.user.api.self;

import com.necrock.readingtracker.security.service.CustomUserDetails;
import com.necrock.readingtracker.user.api.self.dto.SelfUserDetailsDto;
import com.necrock.readingtracker.user.api.self.dto.UpdatePasswordRequest;
import com.necrock.readingtracker.user.api.self.dto.UpdateUserDetailsRequest;
import com.necrock.readingtracker.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.necrock.readingtracker.user.common.UserStatus.DELETED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("api/me")
public class SelfUserController {

    private final UserService service;
    private final SelfUserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public SelfUserController(UserService service, SelfUserMapper mapper, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public SelfUserDetailsDto getUser(@AuthenticationPrincipal CustomUserDetails user) {
        return mapper.toDetailsDto(service.getUser(user.getUsername()));
    }

    @PatchMapping
    public void updateUser(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody UpdateUserDetailsRequest request) {
        service.updateUser(user.getUser().getId(), mapper.toDomainModel(request));
    }

    @PutMapping("/password")
    public void setNewPassword(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody UpdatePasswordRequest request) {
        service.setPassword(user.getUser().getId(), passwordEncoder.encode(request.password()));
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public void deleteUser(@AuthenticationPrincipal CustomUserDetails user) {
        service.setUserStatus(user.getUser().getId(), DELETED);
    }
}
