package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.PaymentDto;
import com.example.bookingsystem.model.Booking;
import com.example.bookingsystem.model.Unit;

import java.util.List;

/**
 * Service interface for managing payments related to bookings.
 */
public interface PaymentService {

    /**
     * Initiates a payment process for the given booking.
     * Creates a payment record with an initial status and sets expiration time.
     *
     * @param booking the booking for which the payment should be initiated
     * @param unit    the unit for which the booking should be initiated
     */
    void initiatePayment(Booking booking, Unit unit);

    /**
     * Processes the payment with the given ID.
     * Used to simulate payment completion or failure.
     *
     * @param paymentId the ID of the payment to be processed
     */
    void processPayment(Long paymentId);

    /**
     * Retrieves all payment records as DTOs.
     *
     * @return list of {@link PaymentDto}
     */
    List<PaymentDto> getAll();
}
