package com.example.bookingsystem.model;

import com.example.bookingsystem.model.enums.AccommodationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entity representing a Unit (accommodation) in the booking system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "units")
public class Unit {
    /**
     * Unique identifier of the unit.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Number of rooms in the unit.
     */
    @Column(name = "number_of_rooms", nullable = false)
    private int numberOfRooms;

    /**
     * Type of accommodation.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccommodationType type;

    /**
     * Floor number where the unit is located.
     */
    @Column(nullable = false)
    private int floor;

    /**
     * Base cost of the unit.
     */
    @Column(name = "base_cost", nullable = false)
    private BigDecimal baseCost;

    /**
     * Total cost of the unit including all markups.
     */
    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;

    /**
     * Optional description of the unit.
     */
    private String description;
}