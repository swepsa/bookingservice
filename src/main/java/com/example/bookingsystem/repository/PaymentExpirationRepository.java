package com.example.bookingsystem.repository;

import com.example.bookingsystem.model.Payment;
import com.example.bookingsystem.model.PaymentExpiration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing {@link PaymentExpiration} entities.
 */
@Repository
public interface PaymentExpirationRepository extends JpaRepository<PaymentExpiration, Long> {
    /**
     * Deletes a PaymentExpiration entity by its associated Payment.
     *
     * @param payment the Payment entity
     */
    void deleteByPayment(Payment payment);

    /**
     * Finds all PaymentExpiration entities where expirationDateTime is before the specified date and time.
     *
     * @param now the cutoff LocalDateTime
     * @return list of expired PaymentExpiration entities
     */
    List<PaymentExpiration> findAllByExpirationDateTimeBefore(LocalDateTime now);
}