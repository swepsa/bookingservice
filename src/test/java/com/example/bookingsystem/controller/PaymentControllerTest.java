package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.PaymentDto;
import com.example.bookingsystem.model.enums.PaymentStatus;
import com.example.bookingsystem.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_shouldReturnListOfPayments() throws Exception {
        PaymentDto payment1 = PaymentDto.builder()
                                        .id(1L)
                                        .amount(new BigDecimal("150.00"))
                                        .status(PaymentStatus.COMPLETED).build();

        PaymentDto payment2 = PaymentDto.builder()
                                        .id(2L)
                                        .amount(new BigDecimal("250.50"))
                                        .status(PaymentStatus.COMPLETED).build();

        List<PaymentDto> payments = List.of(payment1, payment2);

        BDDMockito.given(paymentService.getAll()).willReturn(payments);

        mockMvc.perform(get("/api/payments")
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(payments.size()))
               .andExpect(jsonPath("$[0].id").value(payment1.getId()))
               .andExpect(jsonPath("$[0].amount").value(payment1.getAmount().doubleValue()))
               .andExpect(jsonPath("$[1].id").value(payment2.getId()))
               .andExpect(jsonPath("$[1].amount").value(payment2.getAmount().doubleValue()));
    }
}
