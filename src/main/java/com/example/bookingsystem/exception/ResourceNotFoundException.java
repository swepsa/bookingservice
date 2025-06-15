package com.example.bookingsystem.exception;

/**
 * Exception thrown when a requested resource is not found.
 * Typically used to indicate that an entity with the specified identifier
 * does not exist in the database.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}