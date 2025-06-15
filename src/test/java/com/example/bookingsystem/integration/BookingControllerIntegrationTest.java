package com.example.bookingsystem.integration;

import com.example.bookingsystem.dto.BookingDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllBookings_shouldReturn200AndList() throws Exception {
        String response = mockMvc.perform(get("/api/bookings")
                                         .accept(MediaType.APPLICATION_JSON))
                                 .andExpect(status().isOk())
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString();

        BookingDto[] bookings = objectMapper.readValue(response, BookingDto[].class);

        assertThat(bookings).isNotNull().hasSizeGreaterThanOrEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    void bookUnit_shouldCreateBookingAndReturnIt() throws Exception {
        BookingDto newBooking = BookingDto.builder()
                                          .unitId(1L)
                                          .userId(1L)
                                          .startDate(LocalDate.of(2025, 1, 1))
                                          .endDate(LocalDate.of(2025, 1, 2))
                                          .build();

        String bookingJson = objectMapper.writeValueAsString(newBooking);

        String response = mockMvc.perform(post("/api/bookings")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(bookingJson))
                                 .andExpect(status().isOk())
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString();

        BookingDto created = objectMapper.readValue(response, BookingDto.class);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getUnitId()).isEqualTo(newBooking.getUnitId());
        assertThat(created.getStartDate()).isEqualTo(newBooking.getStartDate());
    }

    @Test
    @Transactional
    @Rollback
    void bookUnit_invalidData_returnsError() throws Exception {
        BookingDto newBooking = BookingDto.builder()
                                          .unitId(-1L)
                                          .userId(1L)
                                          .startDate(LocalDate.of(2025, 1, 1))
                                          .endDate(LocalDate.of(2025, 1, 2))
                                          .build();

        String bookingJson = objectMapper.writeValueAsString(newBooking);

        String response = mockMvc.perform(post("/api/bookings")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(bookingJson))
                                 .andExpect(status().isNotFound())
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = objectMapper.readValue(response, Map.class);

        assertThat(map).isNotNull()
                       .containsEntry("status", HttpStatus.NOT_FOUND.value())
                       .containsEntry("error", "Not Found")
                       .containsEntry("message", "Unit not found: ID = -1");
    }

    @Test
    @Transactional
    @Rollback
    void bookUnit_notAvailableForBooking_returnsError() throws Exception {
        BookingDto newBooking = BookingDto.builder()
                                          .unitId(1L)
                                          .userId(1L)
                                          .startDate(LocalDate.of(2025, 6, 17))
                                          .endDate(LocalDate.of(2025, 6, 21))
                                          .build();

        String bookingJson = objectMapper.writeValueAsString(newBooking);

        String response = mockMvc.perform(post("/api/bookings")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(bookingJson))
                                 .andExpect(status().isConflict())
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = objectMapper.readValue(response, Map.class);

        assertThat(map).isNotNull()
                       .containsEntry("status", HttpStatus.CONFLICT.value())
                       .containsEntry("error", "Unit is not available for booking")
                       .containsEntry("message", "Selected unit is not available for the chosen date range");
    }
}
