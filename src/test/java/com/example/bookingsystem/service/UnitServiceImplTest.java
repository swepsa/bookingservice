package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.UnitDto;
import com.example.bookingsystem.dto.UnitSearchCriteria;
import com.example.bookingsystem.mapper.UnitMapper;
import com.example.bookingsystem.model.Unit;
import com.example.bookingsystem.repository.UnitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class UnitServiceImplTest {

    @Mock
    private UnitRepository unitRepository;
    @Mock
    private UnitMapper unitMapper;
    @InjectMocks
    private UnitServiceImpl unitService;

    @SuppressWarnings("unchecked")
    @Test
    void testGetAvailableUnitsCount_returnCachedCountIfExists() {
        LocalDate start = LocalDate.of(2025, 6, 1);
        LocalDate end = LocalDate.of(2025, 6, 10);

        // Call once to populate the cache
        when(unitRepository.count((Specification<Unit>) any())).thenReturn(5L);
        long firstCall = unitService.getAvailableUnitsCount(start, end);
        long secondCall = unitService.getAvailableUnitsCount(start, end);

        assertThat(firstCall).isEqualTo(5);
        assertThat(secondCall).isEqualTo(5);
        verify(unitRepository).count((Specification<Unit>) any());
    }

    @Test
    void testAddUnit_success() {
        UnitDto dto = UnitDto.builder().description("unique 1").build();
        Unit entity = new Unit();
        Unit saved = new Unit();
        saved.setId(42L);

        when(unitMapper.toEntity(dto)).thenReturn(entity);
        when(unitRepository.save(entity)).thenReturn(saved);
        when(unitMapper.toDto(saved)).thenReturn(UnitDto.builder().description("unique 2").build());

        UnitDto result = unitService.addUnit(dto);

        assertThat(result).isNotNull();
        verify(unitRepository).save(entity);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testSearchUnits_returnsPage() {
        UnitSearchCriteria criteria = UnitSearchCriteria.builder().build();
        criteria.setNumberOfRooms(2);
        criteria.setStartDate(LocalDate.now());
        criteria.setEndDate(LocalDate.now().plusDays(5));

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Unit> unitPage = new PageImpl<>(List.of(new Unit()));

        when(unitRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(unitPage);
        when(unitMapper.toDto(any(Unit.class))).thenReturn(UnitDto.builder().description("unit 1").build());

        Page<UnitDto> result = unitService.searchUnits(criteria, 0, 10, "id", "asc");

        assertThat(result.getContent()).hasSize(1);
        verify(unitRepository).findAll((Specification<Unit>) any(), eq(pageable));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testEvictOverlappingCacheKeys() {
        unitService.getAvailableUnitsCount(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 10));
        unitService.getAvailableUnitsCount(LocalDate.of(2025, 6, 5), LocalDate.of(2025, 6, 15));
        unitService.getAvailableUnitsCount(LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 10));

        unitService.evictBookingCacheByOverlappingDateRange(LocalDate.of(2025, 6, 8), LocalDate.of(2025, 7, 12));

        long remaining = unitService.getAvailableUnitsCount(LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 10));

        verify(unitRepository, times(4)).count((Specification<Unit>) any());
        assertThat(remaining).isZero(); // Because mock returns 0 by default on count
    }
}
