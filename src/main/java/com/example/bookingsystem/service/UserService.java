package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.UserDto;

import java.util.List;

/**
 * Service interface for managing users.
 */
public interface UserService {

    /**
     * Retrieves all users.
     *
     * @return list of all users as {@link UserDto}
     */
    List<UserDto> getAll();
}
