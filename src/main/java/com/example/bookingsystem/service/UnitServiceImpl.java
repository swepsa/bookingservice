package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.UnitDto;
import com.example.bookingsystem.dto.UnitSearchCriteria;
import com.example.bookingsystem.mapper.UnitMapper;
import com.example.bookingsystem.model.Unit;
import com.example.bookingsystem.repository.UnitRepository;
import com.example.bookingsystem.specification.UnitSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final UnitMapper unitMapper;

    private final ConcurrentMap<String, Long> cache = new ConcurrentHashMap<>();

    @Override
    public long getAvailableUnitsCount(LocalDate startDate, LocalDate endDate) {
        String key = generateKey(startDate, endDate);
        log.debug("Checking available units for date range: {} to {}", startDate, endDate);

        long count = cache.computeIfAbsent(key, k -> {
            log.info("Cache miss for key '{}', querying database...", k);
            Specification<Unit> spec = Specification.where(UnitSpecification.isAvailableWithinDates(startDate, endDate));
            long result = unitRepository.count(spec);
            log.info("Found {} available units for key '{}'", result, k);
            return result;
        });

        log.debug("Returning cached count {} for key '{}'", count, key);
        return count;
    }

    @Override
    public UnitDto addUnit(UnitDto dto) {
        log.info("Adding new unit: {}", dto);
        Unit entity = unitMapper.toEntity(dto);
        Unit saved = unitRepository.save(entity);
        log.info("Unit saved with ID: {}", saved.getId());
        return unitMapper.toDto(saved);
    }

    @Override
    public Page<UnitDto> searchUnits(UnitSearchCriteria criteria,
                                     int page, int size, String sortBy, String sortDir) {
        log.info("Searching units with criteria: {}, page: {}, size: {}, sortBy: {}, sortDir: {}",
                criteria, page, size, sortBy, sortDir);

        Specification<Unit> spec = Specification
                .where(UnitSpecification.hasNumberOfRooms(criteria.getNumberOfRooms()))
                .and(UnitSpecification.hasType(criteria.getType()))
                .and(UnitSpecification.hasFloor(criteria.getFloor()))
                .and(UnitSpecification.hasMaxCost(criteria.getMaxCost()))
                .and(UnitSpecification.isAvailableWithinDates(criteria.getStartDate(), criteria.getEndDate()));

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UnitDto> result = unitRepository.findAll(spec, pageable).map(unitMapper::toDto);
        log.debug("Found {} units matching search criteria", result.getTotalElements());
        return result;
    }

    @Override
    public void evictBookingCacheByOverlappingDateRange(LocalDate changedStart, LocalDate changedEnd) {
        log.info("Evicting cache entries overlapping with range: {} to {}", changedStart, changedEnd);
        int before = cache.size();
        cache.keySet().removeIf(key -> overlaps(key, changedStart, changedEnd));
        int after = cache.size();
        log.debug("Cache size before: {}, after eviction: {}", before, after);
    }

    @Contract(pure = true)
    private @NotNull String generateKey(LocalDate startDate, LocalDate endDate) {
        return startDate + "_" + endDate;
    }

    private boolean overlaps(@NotNull String key, LocalDate changedStart, LocalDate changedEnd) {
        String[] parts = key.split("_");
        LocalDate keyStart = LocalDate.parse(parts[0]);
        LocalDate keyEnd = LocalDate.parse(parts[1]);
        return !keyStart.isAfter(changedEnd) && !changedStart.isAfter(keyEnd);
    }
}
