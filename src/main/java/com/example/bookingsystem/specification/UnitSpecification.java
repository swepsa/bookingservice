package com.example.bookingsystem.specification;

import com.example.bookingsystem.model.Booking;
import com.example.bookingsystem.model.Unit;
import com.example.bookingsystem.model.enums.AccommodationType;
import com.example.bookingsystem.model.enums.BookingStatus;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UnitSpecification {

    private UnitSpecification() {
    }

    @Contract(pure = true)
    public static @NotNull Specification<Unit> hasNumberOfRooms(Integer numberOfRooms) {
        return (root, query, criteriaBuilder) -> {
            if (numberOfRooms == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("numberOfRooms"), numberOfRooms);
        };
    }

    @Contract(pure = true)
    public static @NotNull Specification<Unit> hasType(String type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null || type.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("type"), AccommodationType.valueOf(type.toUpperCase()));
        };
    }

    @Contract(pure = true)
    public static @NotNull Specification<Unit> hasFloor(Integer floor) {
        return (root, query, criteriaBuilder) -> {
            if (floor == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("floor"), floor);
        };
    }

    @Contract(pure = true)
    public static @NotNull Specification<Unit> hasMaxCost(BigDecimal maxCost) {
        return (root, query, criteriaBuilder) -> {
            if (maxCost == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("totalCost"), maxCost);
        };
    }

    @Contract(pure = true)
    public static @NotNull Specification<Unit> isAvailableWithinDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null || endDate == null) {
                return criteriaBuilder.conjunction();
            }

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Booking> booking = subquery.from(Booking.class);
            subquery.select(criteriaBuilder.count(booking.get("id")));
            subquery.where(
                    criteriaBuilder.equal(booking.get("unit").get("id"), root.get("id")),
                    criteriaBuilder.notEqual(booking.get("status"), BookingStatus.CANCELLED),
                    criteriaBuilder.or(
                            criteriaBuilder.between(booking.get("startDate"), startDate, endDate),
                            criteriaBuilder.between(booking.get("endDate"), startDate, endDate),
                            criteriaBuilder.and(
                                    criteriaBuilder.lessThanOrEqualTo(booking.get("startDate"), startDate),
                                    criteriaBuilder.greaterThanOrEqualTo(booking.get("endDate"), endDate)
                            )
                    )
            );

            return criteriaBuilder.equal(subquery, 0L);
        };
    }
}