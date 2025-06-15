package com.example.bookingsystem.exception;

/**
 * Thrown to indicate that a unit is not available for booking
 * within the requested date range.
 *
 * <p>This exception is typically used in the booking flow to inform
 * the client (e.g. UI) that the selected unit is already booked or
 * otherwise unavailable during the specified period.</p>
 * <p>
 * Example usage:
 * <pre>
 *     if (unitIsBooked) {
 *         throw new UnitNotAvailableException("Unit is not available");
 *     }
 * </pre>
 */
public class UnitNotAvailableException extends RuntimeException {
    public UnitNotAvailableException(String message) {
        super(message);
    }
}
