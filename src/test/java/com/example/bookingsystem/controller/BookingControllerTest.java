package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.BookingDto;
import com.example.bookingsystem.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_shouldReturnListOfBookings() throws Exception {
        BookingDto booking1 = BookingDto.builder()
                                        .id(1L)
                                        .unitId(101L)
                                        .userId(201L)
                                        .startDate(LocalDate.of(2025, 6, 20))
                                        .endDate(LocalDate.of(2025, 6, 25))
                                        .build();

        BookingDto booking2 = BookingDto.builder()
                                        .id(2L)
                                        .unitId(102L)
                                        .userId(202L)
                                        .startDate(LocalDate.of(2025, 7, 1))
                                        .endDate(LocalDate.of(2025, 7, 5))
                                        .build();

        List<BookingDto> bookings = List.of(booking1, booking2);

        BDDMockito.given(bookingService.getAll()).willReturn(bookings);

        mockMvc.perform(get("/api/bookings")
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(bookings.size()))
               .andExpect(jsonPath("$[0].id").value(booking1.getId()))
               .andExpect(jsonPath("$[0].unitId").value(booking1.getUnitId()))
               .andExpect(jsonPath("$[1].id").value(booking2.getId()))
               .andExpect(jsonPath("$[1].userId").value(booking2.getUserId()));
    }

    @Test
    void bookUnit_shouldCreateBookingAndReturnDto() throws Exception {
        BookingDto requestDto = BookingDto.builder()
                                          .unitId(101L)
                                          .userId(201L)
                                          .startDate(LocalDate.of(2025, 8, 10))
                                          .endDate(LocalDate.of(2025, 8, 15))
                                          .build();

        BookingDto responseDto = BookingDto.builder()
                                           .id(1L)
                                           .unitId(101L)
                                           .userId(201L)
                                           .startDate(requestDto.getStartDate())
                                           .endDate(requestDto.getEndDate())
                                           .build();

        BDDMockito.given(bookingService.bookUnit(requestDto)).willReturn(responseDto);

        mockMvc.perform(post("/api/bookings")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(responseDto.getId()))
               .andExpect(jsonPath("$.unitId").value(responseDto.getUnitId()))
               .andExpect(jsonPath("$.userId").value(responseDto.getUserId()));
    }
}
