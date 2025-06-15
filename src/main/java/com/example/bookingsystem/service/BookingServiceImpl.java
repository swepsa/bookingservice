package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.BookingDto;
import com.example.bookingsystem.exception.ResourceNotFoundException;
import com.example.bookingsystem.exception.UnitNotAvailableException;
import com.example.bookingsystem.mapper.BookingMapper;
import com.example.bookingsystem.model.Booking;
import com.example.bookingsystem.model.Unit;
import com.example.bookingsystem.model.User;
import com.example.bookingsystem.model.enums.BookingStatus;
import com.example.bookingsystem.repository.BookingRepository;
import com.example.bookingsystem.repository.UnitRepository;
import com.example.bookingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final UnitRepository unitRepository;
    private final BookingMapper bookingMapper;
    private final PaymentService paymentService;
    private final UnitService unitService;

    @Override
    public List<BookingDto> getAll() {
        log.info("Fetching all bookings...");
        List<BookingDto> bookings = bookingRepository.findAll()
                                                     .stream()
                                                     .map(bookingMapper::toDto)
                                                     .toList();
        log.info("Found {} bookings", bookings.size());
        return bookings;
    }

    @Transactional
    public BookingDto bookUnit(@NotNull BookingDto dto) {
        log.info("Attempting to book unit with ID: {} for user ID: {}", dto.getUnitId(), dto.getUserId());

        Unit unit = unitRepository.findById(dto.getUnitId())
                                  .orElseThrow(() -> {
                                      log.error("Unit not found: ID = {}", dto.getUnitId());
                                      return new ResourceNotFoundException("Unit not found: ID = " + dto.getUnitId());
                                  });

        User user = userRepository.findById(dto.getUserId())
                                  .orElseThrow(() -> {
                                      log.error("User not found: ID = {}", dto.getUserId());
                                      return new ResourceNotFoundException("User not found: ID = " + dto.getUserId());
                                  });

        boolean isUnavailable = bookingRepository
                .findTopByUnitAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        unit,
                        List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED),
                        dto.getStartDate(),
                        dto.getEndDate()
                ).isPresent();

        if (isUnavailable) {
            log.warn("Unit ID {} is not available between {} and {}", dto.getUnitId(), dto.getStartDate(), dto.getEndDate());
            throw new UnitNotAvailableException("Selected unit is not available for the chosen date range");
        }

        log.info("Unit available. Proceeding with booking...");

        Booking booking = bookingMapper.toEntity(dto, user, unit);
        booking.setStatus(BookingStatus.PENDING);
        booking = bookingRepository.save(booking);

        log.info("Booking saved with ID: {}", booking.getId());

        paymentService.initiatePayment(booking, unit);
        log.info("Payment initiated for booking ID: {}", booking.getId());

        unitService.evictBookingCacheByOverlappingDateRange(dto.getStartDate(), dto.getEndDate());
        log.debug("Cache evicted for unit availability in range {} - {}", dto.getStartDate(), dto.getEndDate());

        return bookingMapper.toDto(booking);
    }
}