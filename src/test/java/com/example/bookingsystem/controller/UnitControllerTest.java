package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.AvailabilityRequest;
import com.example.bookingsystem.dto.UnitDto;
import com.example.bookingsystem.dto.UnitSearchCriteria;
import com.example.bookingsystem.model.enums.AccommodationType;
import com.example.bookingsystem.service.UnitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UnitController.class)
class UnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UnitService unitService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addUnit_shouldReturnSavedUnit() throws Exception {
        UnitDto requestDto = UnitDto.builder()
                                    .id(1L)
                                    .numberOfRooms(2)
                                    .type(AccommodationType.APARTMENT)
                                    .floor(3)
                                    .baseCost(new BigDecimal("100.00")).build();

        BDDMockito.given(unitService.addUnit(any(UnitDto.class))).willReturn(requestDto);

        mockMvc.perform(post("/api/units")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.numberOfRooms").value(2))
               .andExpect(jsonPath("$.type").value("APARTMENT"))
               .andExpect(jsonPath("$.floor").value(3))
               .andExpect(jsonPath("$.baseCost").value(100.00));
    }

    @Test
    void searchUnits_shouldReturnPagedUnits() throws Exception {
        UnitDto unit1 = UnitDto.builder()
                               .id(1L)
                               .numberOfRooms(1)
                               .build();
        UnitDto unit2 = UnitDto.builder()
                               .id(2L)
                               .numberOfRooms(2)
                               .build();

        Page<UnitDto> page = new PageImpl<>(List.of(unit1, unit2));

        BDDMockito.given(unitService.searchUnits(any(UnitSearchCriteria.class), anyInt(), anyInt(), anyString(), anyString()))
                  .willReturn(page);

        mockMvc.perform(get("/api/units")
                       .param("numberOfRooms", "2")
                       .param("page", "0")
                       .param("size", "10")
                       .param("sortBy", "id")
                       .param("sortDir", "asc")
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content.length()").value(2))
               .andExpect(jsonPath("$.content[0].id").value(1L))
               .andExpect(jsonPath("$.content[1].id").value(2L));
    }

    @Test
    void getAvailableUnitsCount_shouldReturnCount() throws Exception {
        AvailabilityRequest request = new AvailabilityRequest(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 15));
        long expectedCount = 5L;

        BDDMockito.given(unitService.getAvailableUnitsCount(any(LocalDate.class), any(LocalDate.class)))
                  .willReturn(expectedCount);

        mockMvc.perform(post("/api/units/availability")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(content().string(String.valueOf(expectedCount)));
    }
}
