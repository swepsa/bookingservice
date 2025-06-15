package com.example.bookingsystem.repository;

import com.example.bookingsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link User} entities.
 * Provides basic CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}