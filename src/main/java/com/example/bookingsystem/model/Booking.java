package com.example.bookingsystem.model;

import com.example.bookingsystem.model.enums.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity representing a booking made by a user for a specific unit.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {

    /**
     * Unique identifier for the booking.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who made the booking.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The unit that is being booked.
     */
    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    /**
     * The start date of the booking.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * The end date of the booking.
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * The current status of the booking.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
}
