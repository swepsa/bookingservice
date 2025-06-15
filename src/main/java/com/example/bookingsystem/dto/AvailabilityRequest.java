package com.example.bookingsystem.dto;

import java.time.LocalDate;

/**
 * Request DTO representing a date range for availability checks.
 *
 * @param startDate the start date of the availability period
 * @param endDate   the end date of the availability period
 */
public record AvailabilityRequest(
        LocalDate startDate, LocalDate endDate) {
}
