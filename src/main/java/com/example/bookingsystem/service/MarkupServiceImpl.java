package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.UnitDto;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class MarkupServiceImpl implements MarkupService {

    /**
     * Fixed markup rate of 15%.
     */
    private static final BigDecimal MARKUP_RATE = BigDecimal.valueOf(0.15);

    @Override
    public BigDecimal calculatePriseWithMarkup(@NotNull UnitDto unit) {
        BigDecimal baseCost = unit.getBaseCost();
        BigDecimal finalPrice = baseCost.multiply(BigDecimal.ONE.add(MARKUP_RATE));

        log.debug("Calculating price with markup: baseCost={}, markupRate={}, finalPrice={}",
                baseCost, MARKUP_RATE, finalPrice);

        return finalPrice;
    }
}
