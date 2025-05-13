package com.necrock.readingtracker.user.api.admin;

import com.necrock.readingtracker.user.api.admin.dto.AdminUserDetailsDto;
import com.necrock.readingtracker.user.service.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminUserMapper {

    AdminUserDetailsDto toDetailsDto(User user);
}
