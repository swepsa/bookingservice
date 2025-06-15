package com.example.bookingsystem.integration;


import com.example.bookingsystem.dto.AvailabilityRequest;
import com.example.bookingsystem.dto.UnitDto;
import com.example.bookingsystem.model.enums.AccommodationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UnitControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    @Rollback
    void addUnit_and_searchUnits_shouldWork() throws Exception {
        UnitDto newUnit = UnitDto.builder()
                                 .numberOfRooms(100)
                                 .type(AccommodationType.APARTMENT)
                                 .floor(2)
                                 .baseCost(new BigDecimal("150.00"))
                                 .description("Nice 100-room apartment")
                                 .build();

        String response = mockMvc.perform(post("/api/units")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(newUnit)))
                                 .andExpect(status().isOk())
                                 .andExpect(jsonPath("$.id").exists())
                                 .andExpect(jsonPath("$.numberOfRooms").value(100))
                                 .andExpect(jsonPath("$.type").value("APARTMENT"))
                                 .andReturn().getResponse().getContentAsString();

        UnitDto savedUnit = objectMapper.readValue(response, UnitDto.class);

        mockMvc.perform(get("/api/units/search")
                       .param("numberOfRooms", "100")
                       .param("page", "0")
                       .param("size", "10")
                       .param("sortBy", "id")
                       .param("sortDir", "asc"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
               .andExpect(jsonPath("$.content[0].id").value(savedUnit.getId()))
               .andExpect(jsonPath("$.content[0].numberOfRooms").value(100));
    }

    @Test
    void getAvailableUnitsCount_shouldReturnCount() throws Exception {
        AvailabilityRequest request = new AvailabilityRequest(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 2)
        );

        mockMvc.perform(post("/api/units/availability")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(content().string(notNullValue()));
    }
}
