package com.example.bookingsystem.repository;

import com.example.bookingsystem.model.Booking;
import com.example.bookingsystem.model.Unit;
import com.example.bookingsystem.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Booking} entity.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Finds the top booking by unit with specified statuses that overlaps given date range.
     *
     * @param unit      the unit to search bookings for
     * @param statuses  list of booking statuses to filter by
     * @param endDate   the end date of the booking period
     * @param startDate the start date of the booking period
     * @return an Optional containing the found Booking or empty if none found
     */
    Optional<Booking> findTopByUnitAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Unit unit, List<BookingStatus> statuses, LocalDate endDate, LocalDate startDate
    );
}