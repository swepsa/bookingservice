package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.PaymentDto;
import com.example.bookingsystem.exception.ResourceNotFoundException;
import com.example.bookingsystem.mapper.PaymentMapper;
import com.example.bookingsystem.model.Booking;
import com.example.bookingsystem.model.Payment;
import com.example.bookingsystem.model.PaymentExpiration;
import com.example.bookingsystem.model.Unit;
import com.example.bookingsystem.model.enums.BookingStatus;
import com.example.bookingsystem.model.enums.PaymentStatus;
import com.example.bookingsystem.repository.BookingRepository;
import com.example.bookingsystem.repository.PaymentExpirationRepository;
import com.example.bookingsystem.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import static com.example.bookingsystem.service.PaymentServiceImpl.EXPIRATION_PAYMENT_MINUTES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PaymentExpirationRepository paymentExpirationRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private Executor virtualThreadExecutor;
    private PaymentServiceImpl paymentService;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Fix the clock to a known time for predictable expiration time
        fixedClock = Clock.fixed(Instant.parse("2025-06-15T10:00:00Z"), ZoneOffset.UTC);
        paymentService = spy(new PaymentServiceImpl(0,
                paymentRepository,
                bookingRepository,
                paymentExpirationRepository,
                paymentMapper,
                virtualThreadExecutor,
                fixedClock));

    }

    @Test
    void initiatePayment_SuccessPath_ShouldScheduleAsyncProcessing() {
        // Arrange
        Unit unit = Unit.builder().totalCost(BigDecimal.valueOf(100.00)).build();
        Booking booking = Booking.builder().id(123L).unit(unit).build();

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setBooking(booking);
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setStatus(PaymentStatus.INITIATED);

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        paymentService.initiatePayment(booking, unit);

        // Assert
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();

        assertEquals(booking, savedPayment.getBooking());
        assertEquals(BigDecimal.valueOf(100.00), savedPayment.getAmount());
        assertEquals(PaymentStatus.INITIATED, savedPayment.getStatus());

        ArgumentCaptor<PaymentExpiration> expirationCaptor = ArgumentCaptor.forClass(PaymentExpiration.class);
        verify(paymentExpirationRepository).save(expirationCaptor.capture());
        PaymentExpiration savedExpiration = expirationCaptor.getValue();
        assertEquals(savedPayment, savedExpiration.getPayment());
        assertEquals(LocalDateTime.now(fixedClock).plusMinutes(EXPIRATION_PAYMENT_MINUTES), savedExpiration.getExpirationDateTime());
    }

    @Test
    void testProcessPayment_success() {
        Booking booking = new Booking();
        booking.setId(10L);
        booking.setStatus(BookingStatus.PENDING);

        Payment payment = new Payment();
        payment.setId(20L);
        payment.setBooking(booking);
        payment.setStatus(PaymentStatus.INITIATED);

        when(paymentRepository.findById(20L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);
        doNothing().when(paymentExpirationRepository).deleteByPayment(payment);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        paymentService.processPayment(20L);

        // Payment status updated
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
        // Booking status updated
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());

        verify(paymentRepository).save(payment);
        verify(paymentExpirationRepository).deleteByPayment(payment);
        verify(bookingRepository).save(booking);
    }

    @Test
    void testProcessPayment_paymentNotFound_throwsException() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> paymentService.processPayment(999L));
        assertTrue(ex.getMessage().contains("Payment not found"));
    }

    @Test
    void testGetAll_returnsPaymentDtos() {
        Payment payment1 = new Payment();
        payment1.setId(1L);
        Payment payment2 = new Payment();
        payment2.setId(2L);

        PaymentDto dto1 = PaymentDto.builder().id(1L).build();
        PaymentDto dto2 = PaymentDto.builder().id(2L).build();

        when(paymentRepository.findAll()).thenReturn(List.of(payment1, payment2));
        when(paymentMapper.toDto(payment1)).thenReturn(dto1);
        when(paymentMapper.toDto(payment2)).thenReturn(dto2);

        List<PaymentDto> result = paymentService.getAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));

        verify(paymentRepository).findAll();
        verify(paymentMapper, times(2)).toDto(any(Payment.class));
    }
}
