package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.UnitDto;
import com.example.bookingsystem.dto.UnitSearchCriteria;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

/**
 * Service interface for managing units (accommodations).
 */
public interface UnitService {

    /**
     * Adds a new unit.
     *
     * @param dto the unit data transfer object
     * @return the saved unit as a DTO
     */
    UnitDto addUnit(UnitDto dto);

    /**
     * Counts the number of available units for the given date range.
     *
     * @param startDate the start date of the availability period
     * @param endDate   the end date of the availability period
     * @return the count of available units
     */
    long getAvailableUnitsCount(LocalDate startDate, LocalDate endDate);

    /**
     * Searches units based on search criteria with pagination and sorting.
     *
     * @param criteria the search criteria encapsulating filters like number of rooms, type, floor, etc.
     * @param page     the page number (zero-based)
     * @param size     the size of the page
     * @param sortBy   the property to sort by
     * @param sortDir  the direction of sort, e.g., "asc" or "desc"
     * @return a paginated list of unit DTOs matching the criteria
     */
    Page<UnitDto> searchUnits(UnitSearchCriteria criteria,
                              int page, int size, String sortBy, String sortDir);

    /**
     * Evicts the booking cache for units overlapping with the specified date range.
     *
     * @param changedStart the start date of the changed period
     * @param changedEnd   the end date of the changed period
     */
    void evictBookingCacheByOverlappingDateRange(LocalDate changedStart, LocalDate changedEnd);
}
