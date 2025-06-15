package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.UnitDto;

import java.math.BigDecimal;

/**
 * Service responsible for calculating the total cost of a unit by applying a markup.
 */
public interface MarkupService {
    /**
     * Calculates the total price of the given unit by applying a predefined markup strategy.
     *
     * @param dto the unit data transfer object containing the base cost and other unit details
     * @return the total cost including markup
     */
    BigDecimal calculatePriseWithMarkup(UnitDto dto);
}
