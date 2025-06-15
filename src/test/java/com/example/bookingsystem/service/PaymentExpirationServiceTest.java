package com.example.bookingsystem.service;

import com.example.bookingsystem.model.Booking;
import com.example.bookingsystem.model.Payment;
import com.example.bookingsystem.model.PaymentExpiration;
import com.example.bookingsystem.model.enums.BookingStatus;
import com.example.bookingsystem.model.enums.PaymentStatus;
import com.example.bookingsystem.repository.BookingRepository;
import com.example.bookingsystem.repository.PaymentExpirationRepository;
import com.example.bookingsystem.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentExpirationServiceTest {

    @Mock
    private PaymentExpirationRepository expirationRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentExpirationService paymentExpirationService;

    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fixedClock = Clock.fixed(Instant.parse("2025-06-15T10:00:00Z"), ZoneId.of("UTC"));
        paymentExpirationService = new PaymentExpirationService(
                expirationRepository,
                bookingRepository,
                paymentRepository,
                fixedClock
        );
    }

    @Test
    void testProcessExpiredPayments_shouldCancelAndFailExpiredOnes() {
        // Given
        LocalDateTime now = LocalDateTime.now(fixedClock);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.PENDING);

        Payment payment = new Payment();
        payment.setId(2L);
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setBooking(booking);

        PaymentExpiration expiration = PaymentExpiration.builder()
                                                        .payment(payment)
                                                        .expirationDateTime(now.minusMinutes(1))
                                                        .build();

        when(expirationRepository.findAllByExpirationDateTimeBefore(now))
                .thenReturn(List.of(expiration));

        // When
        paymentExpirationService.processExpiredPayments();

        // Then
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(PaymentStatus.FAILED, payment.getStatus());

        verify(bookingRepository).save(booking);
        verify(paymentRepository).save(payment);
        verify(expirationRepository).delete(expiration);
    }

    @Test
    void testProcessExpiredPayments_noExpiredPayments_nothingProcessed() {
        LocalDateTime now = LocalDateTime.now(fixedClock);
        when(expirationRepository.findAllByExpirationDateTimeBefore(now)).thenReturn(List.of());

        paymentExpirationService.processExpiredPayments();

        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(paymentRepository);
        verify(expirationRepository, never()).delete(any());
    }
}
