package com.example.bookingsystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entity representing a user in the booking system.
 */
@Data
@Entity
@Table(name = "users")
public class User {
    /**
     * Unique identifier of the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the user.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Email of the user. Must be unique.
     */
    @Column(nullable = false, unique = true)
    private String email;
}