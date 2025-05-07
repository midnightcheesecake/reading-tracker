package com.necrock.readingtracker.user.service;

import com.necrock.readingtracker.user.persistence.UserEntity;
import com.necrock.readingtracker.user.service.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    UserEntity toEntity(User user);

    User toDomainModel(UserEntity userEntity);
}
