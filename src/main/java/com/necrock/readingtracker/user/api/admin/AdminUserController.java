package com.necrock.readingtracker.user.api.admin;

import com.necrock.readingtracker.user.api.admin.dto.UpdateUserRoleRequest;
import com.necrock.readingtracker.user.api.admin.dto.UpdateUserStatusRequest;
import com.necrock.readingtracker.user.api.admin.dto.AdminUserDetailsDto;
import com.necrock.readingtracker.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
public class AdminUserController {

    private final UserService service;
    private final AdminUserMapper mapper;

    public AdminUserController(UserService service, AdminUserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public AdminUserDetailsDto getUser(@PathVariable Long id) {
        return mapper.toDetailsDto(service.getUser(id));
    }

    @PutMapping("/{id}/status")
    public void setUserStatus(@PathVariable Long id, @Valid @RequestBody UpdateUserStatusRequest request) {
        service.setUserStatus(id, request.status());
    }

    @PutMapping("/{id}/role")
    public void setUserRole(@PathVariable Long id, @Valid @RequestBody UpdateUserRoleRequest request) {
        service.assignUserRole(id, request.role());
    }
}
