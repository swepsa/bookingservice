package com.example.bookingsystem.dto;

/**
 * Data Transfer Object representing a user in the booking system.
 *
 * @param id    the unique identifier of the user
 * @param name  the name of the user
 * @param email the email address of the user
 */
public record UserDto(
        Long id,
        String name,
        String email
) {
}
