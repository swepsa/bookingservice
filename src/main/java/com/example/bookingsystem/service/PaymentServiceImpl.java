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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    protected static final long EXPIRATION_PAYMENT_MINUTES = 2;
    private final double paymentSuccessProbability;

    private static final long PAYMENT_PROCESSING_DELAY_MINUTES = 2;

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentExpirationRepository paymentExpirationRepository;
    private final PaymentMapper paymentMapper;
    private final Executor virtualThreadExecutor;
    private final Clock clock;

    public PaymentServiceImpl(@Value("${payment.success.probability}") double paymentSuccessProbability,
                              PaymentRepository paymentRepository, BookingRepository bookingRepository,
                              PaymentExpirationRepository paymentExpirationRepository, PaymentMapper paymentMapper,
                              Executor virtualThreadExecutor, Clock clock) {
        this.paymentSuccessProbability = paymentSuccessProbability;
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.paymentExpirationRepository = paymentExpirationRepository;
        this.paymentMapper = paymentMapper;
        this.virtualThreadExecutor = virtualThreadExecutor;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void initiatePayment(Booking booking, Unit unit) {
        log.info("Initiating payment for booking ID: {}", booking.getId());

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(unit.getTotalCost());
        payment.setStatus(PaymentStatus.INITIATED);
        paymentRepository.save(payment);
        log.debug("Saved payment with ID: {} and status: {}", payment.getId(), payment.getStatus());

        LocalDateTime expirationTime = LocalDateTime.now(clock).plusMinutes(PAYMENT_PROCESSING_DELAY_MINUTES);
        PaymentExpiration expiration = PaymentExpiration.builder()
                                                        .payment(payment)
                                                        .expirationDateTime(expirationTime)
                                                        .build();
        paymentExpirationRepository.save(expiration);
        log.debug("Saved expiration for payment ID: {} at {}", payment.getId(), expirationTime);

        if (Math.random() < paymentSuccessProbability) {
            log.info("Payment ID: {} will be processed asynchronously after {} minutes", payment.getId(), PAYMENT_PROCESSING_DELAY_MINUTES);
            CompletableFuture.runAsync(
                    () -> processPayment(payment.getId()),
                    CompletableFuture.delayedExecutor(PAYMENT_PROCESSING_DELAY_MINUTES, TimeUnit.MINUTES, virtualThreadExecutor)
            );
        } else {
            log.warn("Simulated payment failure for payment ID: {}", payment.getId());
        }
    }

    @Transactional
    public void processPayment(Long paymentId) {
        log.info("Processing payment ID: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                                           .orElseThrow(() -> {
                                               log.error("Payment not found for ID: {}", paymentId);
                                               return new ResourceNotFoundException("Payment not found: ID = " + paymentId);
                                           });

        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);
        log.info("Payment ID: {} marked as COMPLETED", paymentId);

        paymentExpirationRepository.deleteByPayment(payment);
        log.debug("Deleted expiration record for payment ID: {}", paymentId);

        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        log.info("Booking ID: {} marked as CONFIRMED", booking.getId());
    }

    @Override
    public List<PaymentDto> getAll() {
        log.info("Fetching all payments");
        return paymentRepository.findAll()
                                .stream()
                                .map(paymentMapper::toDto)
                                .toList();
    }
}
