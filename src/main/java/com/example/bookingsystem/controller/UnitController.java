package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.AvailabilityRequest;
import com.example.bookingsystem.dto.UnitDto;
import com.example.bookingsystem.dto.UnitSearchCriteria;
import com.example.bookingsystem.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * REST controller for managing units.
 */
@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
@Tag(name = "Units", description = "Endpoints for managing units")
public class UnitController {

    private final UnitService unitService;

    /**
     * Adds a new unit.
     *
     * @param dto unit data
     * @return saved unit DTO
     */
    @Operation(
            summary = "Add a new unit",
            description = "Creates a new unit with the provided details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Unit created successfully")
            }
    )
    @PostMapping
    public ResponseEntity<UnitDto> addUnit(@RequestBody UnitDto dto) {
        UnitDto savedUnit = unitService.addUnit(dto);
        return ResponseEntity.ok(savedUnit);
    }

    /**
     * Searches units with optional filters, pagination and sorting.
     *
     * @param numberOfRooms number of rooms filter
     * @param type          unit type filter
     * @param floor         floor filter
     * @param startDate     availability start date filter
     * @param endDate       availability end date filter
     * @param maxCost       maximum cost filter
     * @param page          page number (default 0)
     * @param size          page size (default 10)
     * @param sortBy        sorting field (default "id")
     * @param sortDir       sorting direction: asc or desc (default "asc")
     * @return paginated list of units matching filters
     */
    @Operation(
            summary = "Search units",
            description = "Returns paginated list of units filtered by various parameters",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Units retrieved successfully")
            }
    )
    @GetMapping
    public ResponseEntity<Page<UnitDto>> searchUnits(
            @RequestParam(required = false) Integer numberOfRooms,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BigDecimal maxCost,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        UnitSearchCriteria criteria = UnitSearchCriteria.builder()
                                                        .numberOfRooms(numberOfRooms)
                                                        .type(type)
                                                        .floor(floor)
                                                        .startDate(startDate)
                                                        .endDate(endDate)
                                                        .maxCost(maxCost)
                                                        .build();
        Page<UnitDto> units = unitService.searchUnits(criteria, page, size, sortBy, sortDir);
        return ResponseEntity.ok(units);
    }

    /**
     * Returns the count of units available between given dates.
     *
     * @param request contains startDate and endDate
     * @return number of available units
     */
    @Operation(
            summary = "Get available units count",
            description = "Returns the count of units available within the specified date range",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Count returned successfully")
            }
    )
    @PostMapping("/availability")
    public ResponseEntity<Long> getAvailableUnitsCount(@RequestBody @NotNull AvailabilityRequest request) {
        long count = unitService.getAvailableUnitsCount(request.startDate(), request.endDate());
        return ResponseEntity.ok(count);
    }
}
