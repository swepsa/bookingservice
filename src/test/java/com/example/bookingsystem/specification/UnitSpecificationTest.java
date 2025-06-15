package com.example.bookingsystem.specification;

import com.example.bookingsystem.model.Booking;
import com.example.bookingsystem.model.Unit;
import com.example.bookingsystem.model.enums.AccommodationType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UnitSpecificationTest {

    @Mock
    private Root<Unit> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder criteriaBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHasNumberOfRooms_NullInput_ReturnsConjunction() {
        Specification<Unit> spec = UnitSpecification.hasNumberOfRooms(null);

        when(criteriaBuilder.conjunction()).thenReturn(mock(Predicate.class));
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
        verify(criteriaBuilder).conjunction();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testHasNumberOfRooms_ValidInput_ReturnsEqualPredicate() {
        Specification<Unit> spec = UnitSpecification.hasNumberOfRooms(3);

        when(root.get("numberOfRooms")).thenReturn(mock(Path.class));
        when(criteriaBuilder.equal(any(), eq(3))).thenReturn(mock(Predicate.class));

        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
        verify(criteriaBuilder).equal(any(), eq(3));
    }

    @Test
    void testHasType_NullOrEmpty_ReturnsConjunction() {
        Specification<Unit> specNull = UnitSpecification.hasType(null);
        Specification<Unit> specEmpty = UnitSpecification.hasType("");

        when(criteriaBuilder.conjunction()).thenReturn(mock(Predicate.class));

        assertNotNull(specNull.toPredicate(root, query, criteriaBuilder));
        assertNotNull(specEmpty.toPredicate(root, query, criteriaBuilder));
        verify(criteriaBuilder, times(2)).conjunction();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testHasType_ValidType_ReturnsEqualPredicate() {
        Specification<Unit> spec = UnitSpecification.hasType("APARTMENT");

        when(root.get("type")).thenReturn(mock(Path.class));
        when(criteriaBuilder.equal(any(), eq(AccommodationType.APARTMENT))).thenReturn(mock(Predicate.class));

        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
        verify(criteriaBuilder).equal(any(), eq(AccommodationType.APARTMENT));
    }

    @Test
    void testHasFloor_NullInput_ReturnsConjunction() {
        Specification<Unit> spec = UnitSpecification.hasFloor(null);

        when(criteriaBuilder.conjunction()).thenReturn(mock(Predicate.class));
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
        verify(criteriaBuilder).conjunction();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testHasFloor_ValidInput_ReturnsEqualPredicate() {
        Specification<Unit> spec = UnitSpecification.hasFloor(5);

        when(root.get("floor")).thenReturn(mock(Path.class));
        when(criteriaBuilder.equal(any(), eq(5))).thenReturn(mock(Predicate.class));

        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
        verify(criteriaBuilder).equal(any(), eq(5));
    }

    @Test
    void testHasMaxCost_NullInput_ReturnsConjunction() {
        Specification<Unit> spec = UnitSpecification.hasMaxCost(null);

        when(criteriaBuilder.conjunction()).thenReturn(mock(Predicate.class));
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
        verify(criteriaBuilder).conjunction();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testHasMaxCost_ValidInput_ReturnsLessThanOrEqualToPredicate() {
        Specification<Unit> spec = UnitSpecification.hasMaxCost(BigDecimal.TEN);

        when(root.get("totalCost")).thenReturn(mock(Path.class));
        when(criteriaBuilder.lessThanOrEqualTo(any(), eq(BigDecimal.TEN))).thenReturn(mock(Predicate.class));

        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
        verify(criteriaBuilder).lessThanOrEqualTo(any(), eq(BigDecimal.TEN));
    }

    @Test
    void testIsAvailableWithinDates_NullDates_ReturnsConjunction() {
        Specification<Unit> spec = UnitSpecification.isAvailableWithinDates(null, null);

        when(criteriaBuilder.conjunction()).thenReturn(mock(Predicate.class));
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
        verify(criteriaBuilder).conjunction();
    }

    @Test
    void testIsAvailableWithinDates_NullStartDate_ReturnsConjunction() {
        Specification<Unit> spec = UnitSpecification.isAvailableWithinDates(LocalDate.of(2025, 6, 1), null);

        when(criteriaBuilder.conjunction()).thenReturn(mock(Predicate.class));
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
        verify(criteriaBuilder).conjunction();
    }

    @Test
    void testIsAvailableWithinDates_NullEndDate_ReturnsConjunction() {
        Specification<Unit> spec = UnitSpecification.isAvailableWithinDates(null, LocalDate.of(2025, 6, 1));

        when(criteriaBuilder.conjunction()).thenReturn(mock(Predicate.class));
        Predicate predicate = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
        verify(criteriaBuilder).conjunction();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testIsAvailableWithinDates_ValidDates_BuildsSubquery() {
        LocalDate start = LocalDate.of(2025, 6, 1);
        LocalDate end = LocalDate.of(2025, 6, 10);

        Subquery<Long> subquery = mock(Subquery.class);
        Root<Booking> bookingRoot = mock(Root.class);
        Path<Object> unitIdPath = mock(Path.class);
        Path<Object> statusPath = mock(Path.class);
        Path<Object> startDatePath = mock(Path.class);
        Path<Object> endDatePath = mock(Path.class);

        when(query.subquery(Long.class)).thenReturn(subquery);
        when(subquery.from(Booking.class)).thenReturn(bookingRoot);
        when(subquery.select(any())).thenReturn(subquery);
        when(bookingRoot.get("id")).thenReturn(mock(Path.class));
        when(bookingRoot.get("unit")).thenReturn(mock(Path.class));
        when(bookingRoot.get("unit").get("id")).thenReturn(unitIdPath);
        when(root.get("id")).thenReturn(unitIdPath);
        when(bookingRoot.get("status")).thenReturn(statusPath);
        when(bookingRoot.get("startDate")).thenReturn(startDatePath);
        when(bookingRoot.get("endDate")).thenReturn(endDatePath);

        when(criteriaBuilder.count(any())).thenReturn(mock(Expression.class));
        when(criteriaBuilder.equal(any(), eq(0L))).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.notEqual(any(), any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.or(any(), any(), any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.between(any(), any(LocalDate.class), any(LocalDate.class))).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.and(any(), any())).thenReturn(mock(Predicate.class));

        Predicate predicate = UnitSpecification.isAvailableWithinDates(start, end).toPredicate(root, query, criteriaBuilder);

        assertNotNull(predicate);
        verify(query).subquery(Long.class);
        verify(criteriaBuilder).equal(any(), eq(0L));
    }
}
