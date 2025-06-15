package com.example.bookingsystem.mapper;

import com.example.bookingsystem.dto.BookingDto;
import com.example.bookingsystem.model.Booking;
import com.example.bookingsystem.model.Unit;
import com.example.bookingsystem.model.User;
import org.mapstruct.Context;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between {@link Booking} entity and {@link BookingDto}.
 */
@Mapper(componentModel = "spring")
public interface BookingMapper {

    /**
     * Converts Booking entity to BookingDto.
     *
     * @param booking the Booking entity
     * @return mapped BookingDto
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "unit.id", target = "unitId")
    BookingDto toDto(Booking booking);

    /**
     * Converts BookingDto to Booking entity.
     * Requires User and Unit context objects for mapping associations.
     *
     * @param dto  the BookingDto
     * @param user the User entity associated with the booking
     * @param unit the Unit entity associated with the booking
     * @return mapped Booking entity
     */
    @InheritInverseConfiguration
    Booking toEntity(BookingDto dto, @Context User user, @Context Unit unit);
}
