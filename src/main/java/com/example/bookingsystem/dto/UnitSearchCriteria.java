package com.example.bookingsystem.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO representing search criteria for Units.
 */
@Data
@Builder
public class UnitSearchCriteria {
    /**
     * Number of rooms requested in the unit.
     */
    private Integer numberOfRooms;

    /**
     * Accommodation type as a string (e.g., "APARTMENT", "HOUSE").
     * Consider using an enum for stricter typing.
     */
    private String type;

    /**
     * Floor number where the unit is located.
     */
    private Integer floor;

    /**
     * Start date of the desired booking period.
     */
    private LocalDate startDate;

    /**
     * End date of the desired booking period.
     */
    private LocalDate endDate;

    /**
     * Maximum acceptable cost for the unit.
     */
    private BigDecimal maxCost;

}
