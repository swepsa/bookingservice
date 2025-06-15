package com.example.bookingsystem.mapper;

import com.example.bookingsystem.dto.UserDto;
import com.example.bookingsystem.model.User;
import org.mapstruct.Mapper;

/**
 * Mapper for converting between {@link User} entity and {@link UserDto}.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts User entity to UserDto.
     *
     * @param entity the User entity
     * @return the corresponding UserDto
     */
    UserDto toDto(User entity);

    /**
     * Converts UserDto to User entity.
     *
     * @param dto the UserDto
     * @return the corresponding User entity
     */
    User toEntity(UserDto dto);
}
