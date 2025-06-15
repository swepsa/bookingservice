package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.UnitDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MarkupServiceImplTest {

    @Autowired
    private MarkupService markupService;

    @Test
    void calculatePriseWithMarkup_shouldAdd15PercentToBaseCost() {
        // Given
        UnitDto unit = UnitDto.builder()
                              .baseCost(new BigDecimal(100))
                              .build();

        // When
        BigDecimal result = markupService.calculatePriseWithMarkup(unit);

        // Then
        assertEquals(0, result.compareTo(new BigDecimal("115.00")));
    }

    @Test
    void calculatePriseWithMarkup_shouldReturnZeroWhenBaseCostIsZero() {
        // Given
        UnitDto unit = UnitDto.builder()
                              .baseCost(BigDecimal.ZERO)
                              .build();

        // When
        BigDecimal result = markupService.calculatePriseWithMarkup(unit);

        // Then
        assertEquals(0, result.compareTo(BigDecimal.ZERO));
    }

    @Test
    void calculatePriseWithMarkup_shouldHandleDecimalValuesCorrectly() {
        // Given
        UnitDto unit = UnitDto.builder()
                              .baseCost(new BigDecimal("123.45"))
                              .build();

        // When
        BigDecimal result = markupService.calculatePriseWithMarkup(unit);

        // Then
        assertEquals(new BigDecimal("141.9675"), result);
    }
}
