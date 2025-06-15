package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.BookingDto;
import com.example.bookingsystem.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing bookings.
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Endpoints for managing bookings")
public class BookingController {

    private final BookingService bookingService;

    /**
     * Retrieves all bookings.
     *
     * @return list of BookingDto objects
     */
    @Operation(summary = "Get all bookings", description = "Returns a list of all bookings")
    @GetMapping
    public List<BookingDto> getAll() {
        return bookingService.getAll();
    }

    /**
     * Creates a new booking for a unit.
     *
     * @param dto booking details
     * @return created BookingDto
     */
    @Operation(summary = "Create a new booking", description = "Books a unit with provided booking details")
    @PostMapping
    public ResponseEntity<BookingDto> bookUnit(@RequestBody BookingDto dto) {
        BookingDto booking = bookingService.bookUnit(dto);
        return ResponseEntity.ok(booking);
    }
}
