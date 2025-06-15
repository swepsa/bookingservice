package com.example.bookingsystem.dto;

import com.example.bookingsystem.model.enums.AccommodationType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing a unit.
 */
@Data
@Builder
public class UnitDto {

    /**
     * The unique identifier of the unit.
     */
    private Long id;

    /**
     * The number of rooms in the unit.
     */
    private int numberOfRooms;

    /**
     * The type of accommodation (e.g., HOME, FLAT, APARTMENTS).
     */
    private AccommodationType type;

    /**
     * The floor on which the unit is located.
     */
    private int floor;

    /**
     * The base cost of the unit before any markup.
     */
    private BigDecimal baseCost;

    /**
     * The total cost of the unit after markup or adjustments.
     */
    private BigDecimal totalCost;

    /**
     * A brief description of the unit.
     */
    private String description;
}
