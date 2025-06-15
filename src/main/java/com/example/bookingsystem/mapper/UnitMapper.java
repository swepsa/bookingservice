package com.example.bookingsystem.mapper;

import com.example.bookingsystem.dto.UnitDto;
import com.example.bookingsystem.model.Unit;
import com.example.bookingsystem.service.MarkupService;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper for converting between {@link Unit} entity and {@link UnitDto}.
 * Uses {@link MarkupService} to calculate total cost after mapping.
 */
@Mapper(componentModel = "spring")
public abstract class UnitMapper {

    @Autowired
    protected MarkupService markupService;

    /**
     * Converts UnitDto to Unit entity.
     *
     * @param dto the UnitDto
     * @return the Unit entity
     */
    public abstract Unit toEntity(UnitDto dto);

    /**
     * Converts Unit entity to UnitDto.
     *
     * @param entity the Unit entity
     * @return the UnitDto
     */
    public abstract UnitDto toDto(Unit entity);

    /**
     * After mapping from DTO to entity builder,
     * calculate and set the total cost with markup.
     *
     * @param dto     the source DTO
     * @param builder the target Unit builder
     */
    @AfterMapping
    protected void afterToEntity(UnitDto dto, @MappingTarget @NotNull Unit.UnitBuilder builder) {
        builder.totalCost(markupService.calculatePriseWithMarkup(dto));
    }

}
