package com.example.bookingsystem.service;

import com.example.bookingsystem.model.Booking;
import com.example.bookingsystem.model.Payment;
import com.example.bookingsystem.model.PaymentExpiration;
import com.example.bookingsystem.model.enums.BookingStatus;
import com.example.bookingsystem.model.enums.PaymentStatus;
import com.example.bookingsystem.repository.BookingRepository;
import com.example.bookingsystem.repository.PaymentExpirationRepository;
import com.example.bookingsystem.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentExpirationService {


    private static final long FIXED_RATE = 15 * 60 * 1000L;    // every 15 minutes

    private final PaymentExpirationRepository expirationRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final Clock clock;

    @Scheduled(fixedRate = FIXED_RATE)
    @Transactional
    public void processExpiredPayments() {
        LocalDateTime now = LocalDateTime.now(clock);
        log.info("Running payment expiration task at {}", now);

        List<PaymentExpiration> expiredList = expirationRepository.findAllByExpirationDateTimeBefore(now);
        log.debug("Found {} expired payment(s)", expiredList.size());

        for (PaymentExpiration expiration : expiredList) {
            Payment payment = expiration.getPayment();
            Booking booking = payment.getBooking();

            log.info("Processing expired payment with ID: {}, booking ID: {}",
                    payment.getId(), booking.getId());

            // Update booking status
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            log.debug("Cancelled booking ID: {}", booking.getId());

            // Update payment status
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.debug("Marked payment ID: {} as FAILED", payment.getId());

            // Remove expiration record
            expirationRepository.delete(expiration);
            log.debug("Deleted expiration record for payment ID: {}", payment.getId());
        }

        log.info("Payment expiration task completed");
    }
}
