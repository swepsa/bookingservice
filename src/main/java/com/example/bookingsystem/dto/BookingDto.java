package com.example.bookingsystem.dto;

import com.example.bookingsystem.model.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a booking.
 */
@Data
@Builder
public class BookingDto {

    /**
     * The unique identifier of the booking.
     */
    private Long id;

    /**
     * The ID of the user who made the booking.
     */
    private Long userId;

    /**
     * The ID of the unit that is booked.
     */
    private Long unitId;

    /**
     * The start date of the booking period.
     */
    private LocalDate startDate;

    /**
     * The end date of the booking period.
     */
    private LocalDate endDate;

    /**
     * The current status of the booking (e.g., PENDING, CONFIRMED, CANCELLED).
     */
    private BookingStatus status;
}

