package com.example.bookingsystem.repository;

import com.example.bookingsystem.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Unit} entities.
 * Provides CRUD operations and supports Specifications for complex queries.
 */
@Repository
public interface UnitRepository extends JpaRepository<Unit, Long>, JpaSpecificationExecutor<Unit> {
}