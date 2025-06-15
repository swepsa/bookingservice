package com.example.bookingsystem.util;

import com.example.bookingsystem.dto.UnitDto;
import com.example.bookingsystem.model.Booking;
import com.example.bookingsystem.model.Payment;
import com.example.bookingsystem.model.PaymentExpiration;
import com.example.bookingsystem.model.enums.AccommodationType;
import com.example.bookingsystem.model.enums.PaymentStatus;
import com.example.bookingsystem.repository.BookingRepository;
import com.example.bookingsystem.repository.PaymentExpirationRepository;
import com.example.bookingsystem.repository.PaymentRepository;
import com.example.bookingsystem.service.UnitService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Component responsible for preloading test data into the application at startup.
 * It inserts a set of {@link UnitDto} entries and payment records based on existing bookings.
 */
@Component
@RequiredArgsConstructor
public class DataLoader {

    private final UnitService unitService;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentExpirationRepository paymentExpirationRepository;
    private final Clock clock;

    /**
     * Invoked after the bean initialization to populate initial data.
     */
    @PostConstruct
    public void loadData() {
        loadUnits();
        loadPayments();
    }

    private void loadUnits() {
        Random random = new Random();
        AccommodationType[] types = AccommodationType.values();

        for (int i = 0; i < 90; i++) {
            UnitDto unit = UnitDto.builder()
                                  .numberOfRooms(random.nextInt(5) + 1)
                                  .type(types[random.nextInt(types.length)])
                                  .floor(random.nextInt(10) + 1)
                                  .baseCost(BigDecimal.valueOf(50 + (150 - 50) * random.nextDouble())
                                                      .setScale(2, RoundingMode.HALF_UP))
                                  .description("Auto-generated unit " + (i + 1)).build();
            unitService.addUnit(unit);
        }
    }

    private void loadPayments() {
        LocalDateTime expirationTime = LocalDateTime.now(clock);
        bookingRepository.findAll().forEach(booking -> {

            switch (booking.getStatus()) {
                case PENDING -> {
                    Payment payment = paymentRepository.save(createPayment(booking, PaymentStatus.INITIATED));
                    paymentExpirationRepository.save(PaymentExpiration.builder().payment(payment).expirationDateTime(expirationTime).build());
                }
                case CONFIRMED -> paymentRepository.save(createPayment(booking, PaymentStatus.COMPLETED));
                case CANCELLED -> paymentRepository.save(createPayment(booking, PaymentStatus.FAILED));
            }
        });
    }

    private Payment createPayment(Booking booking, PaymentStatus status) {
        return Payment.builder().booking(booking).amount(booking.getUnit().getTotalCost()).status(status).build();
    }
}