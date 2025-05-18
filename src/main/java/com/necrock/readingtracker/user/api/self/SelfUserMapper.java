package com.necrock.readingtracker.user.api.self;

import com.necrock.readingtracker.user.api.self.dto.SelfUserDetailsDto;
import com.necrock.readingtracker.user.api.self.dto.UpdateUserDetailsRequest;
import com.necrock.readingtracker.user.service.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SelfUserMapper {

    SelfUserDetailsDto toDetailsDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toDomainModel(UpdateUserDetailsRequest dto);
}
