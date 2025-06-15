package com.example.bookingsystem.mapper;

import com.example.bookingsystem.dto.PaymentDto;
import com.example.bookingsystem.model.Booking;
import com.example.bookingsystem.model.Payment;
import org.mapstruct.Context;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between {@link Payment} entity and {@link PaymentDto}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    /**
     * Converts Payment entity to PaymentDto.
     *
     * @param entity the Payment entity
     * @return mapped PaymentDto
     */
    @Mapping(source = "booking.id", target = "bookingId")
    PaymentDto toDto(Payment entity);

    /**
     * Converts PaymentDto to Payment entity.
     * Requires Booking context for association.
     *
     * @param dto     the PaymentDto
     * @param booking the Booking entity associated with the payment
     * @return mapped Payment entity
     */
    @InheritInverseConfiguration
    Payment toEntity(PaymentDto dto, @Context Booking booking);
}