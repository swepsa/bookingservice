package com.example.bookingsystem.repository;

import com.example.bookingsystem.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Payment} entities.
 * Extends JpaRepository to provide CRUD operations on Payment.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}