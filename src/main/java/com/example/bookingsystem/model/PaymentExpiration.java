package com.example.bookingsystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing the expiration information of a Payment.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_expirations")
public class PaymentExpiration {
    /**
     * Unique identifier of the payment expiration record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Payment associated with this expiration.
     */
    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    /**
     * Expiration date and time of the payment.
     */
    @Column(name = "expiration_date_time", nullable = false)
    private LocalDateTime expirationDateTime;
}