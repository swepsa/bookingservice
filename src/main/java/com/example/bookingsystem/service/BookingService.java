package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.BookingDto;

import java.util.List;

/**
 * Service interface for managing bookings.
 */
public interface BookingService {

    /**
     * Retrieves all bookings.
     *
     * @return list of all {@link BookingDto}
     */
    List<BookingDto> getAll();

    /**
     * Creates a new booking for a unit.
     *
     * @param dto booking details
     * @return created {@link BookingDto}
     */
    BookingDto bookUnit(BookingDto dto);
}
