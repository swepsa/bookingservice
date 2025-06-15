package com.example.bookingsystem.dto;

import com.example.bookingsystem.model.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing a payment.
 */
@Data
@Builder
public class PaymentDto {

    /**
     * The unique identifier of the payment.
     */
    private Long id;

    /**
     * The ID of the booking associated with this payment.
     */
    private Long bookingId;

    /**
     * The total amount of the payment.
     */
    private BigDecimal amount;

    /**
     * The current status of the payment (e.g., PENDING, COMPLETED, FAILED).
     */
    private PaymentStatus status;
}
