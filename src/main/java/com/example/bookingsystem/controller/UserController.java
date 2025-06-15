package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.UserDto;
import com.example.bookingsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing users in the system.
 * Provides endpoints to retrieve user data.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    /**
     * Retrieves a list of all users.
     *
     * @return list of {@link UserDto} representing all users
     */
    @Operation(
            summary = "Get all users",
            description = "Returns a list of all users in the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
            }
    )
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAll();
    }
}

