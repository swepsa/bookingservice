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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class BookingServiceImplTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UnitRepository unitRepository;

    @Mock
    BookingMapper bookingMapper;

    @Mock
    PaymentService paymentService;

    @Mock
    UnitService unitService;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Captor
    ArgumentCaptor<Booking> bookingCaptor;

    private BookingDto bookingDto;
    private Unit unit;
    private User user;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingDto = BookingDto.builder()
                               .unitId(1L)
                               .userId(2L)
                               .startDate(LocalDate.of(2025, 7, 1))
                               .endDate(LocalDate.of(2025, 7, 5))
                               .build();

        unit = new Unit();
        unit.setId(1L);

        user = new User();
        user.setId(2L);

        booking = new Booking();
        booking.setId(10L);
        booking.setUnit(unit);
        booking.setUser(user);
        booking.setStatus(BookingStatus.PENDING);
        booking.setStartDate(bookingDto.getStartDate());
        booking.setEndDate(bookingDto.getEndDate());
    }

    @Test
    void testGetAll_returnAllBookingsMappedToDto() {
        BookingDto dto1 = BookingDto.builder().build();
        BookingDto dto2 = BookingDto.builder().build();

        List<Booking> bookings = List.of(booking, booking);
        given(bookingRepository.findAll()).willReturn(bookings);
        given(bookingMapper.toDto(any(Booking.class))).willReturn(dto1, dto2);

        List<BookingDto> result = bookingService.getAll();

        then(bookingRepository).should().findAll();
        then(bookingMapper).should(times(2)).toDto(any(Booking.class));

        assertThat(result).hasSize(2).containsExactly(dto1, dto2);
    }

    @Test
    void testBookUnit_shouldBookUnitWhenAvailable() {
        given(unitRepository.findById(bookingDto.getUnitId())).willReturn(Optional.of(unit));
        given(userRepository.findById(bookingDto.getUserId())).willReturn(Optional.of(user));
        given(bookingRepository.findTopByUnitAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                any(Unit.class),
                anyList(),
                any(LocalDate.class),
                any(LocalDate.class)
        )).willReturn(Optional.empty());

        given(bookingMapper.toEntity(bookingDto, user, unit)).willReturn(booking);
        given(bookingRepository.save(any(Booking.class))).willReturn(booking);
        given(bookingMapper.toDto(any(Booking.class))).willReturn(bookingDto);

        BookingDto result = bookingService.bookUnit(bookingDto);

        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();

        assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(result).isEqualTo(bookingDto);

        verify(paymentService).initiatePayment(savedBooking, unit);
        verify(unitService).evictBookingCacheByOverlappingDateRange(bookingDto.getStartDate(), bookingDto.getEndDate());
    }

    @Test
    void testBookUnit_shouldThrowResourceNotFoundException_WhenUnitNotFound() {
        given(unitRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.bookUnit(bookingDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Unit not found");
    }

    @Test
    void testBookUnit_shouldThrowResourceNotFoundException_WhenUserNotFound() {
        given(unitRepository.findById(anyLong())).willReturn(Optional.of(unit));
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.bookUnit(bookingDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void testBookUnit_shouldThrowUnitNotAvailableException_WhenUnitIsAlreadyBooked() {
        given(unitRepository.findById(bookingDto.getUnitId())).willReturn(Optional.of(unit));
        given(userRepository.findById(bookingDto.getUserId())).willReturn(Optional.of(user));

        // simulate existing booking with overlapping dates
        given(bookingRepository.findTopByUnitAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                unit,
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED),
                bookingDto.getStartDate(),
                bookingDto.getEndDate()
        )).willReturn(Optional.of(new Booking()));

        assertThatThrownBy(() -> bookingService.bookUnit(bookingDto))
                .isInstanceOf(UnitNotAvailableException.class)
                .hasMessageContaining("Selected unit is not available");
    }
}
