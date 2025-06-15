package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.PaymentDto;
import com.example.bookingsystem.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing payments.
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Endpoints for managing payments")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Retrieves all payments.
     *
     * @return list of PaymentDto objects
     */
    @Operation(summary = "Get all payments", description = "Returns a list of all payments")
    @GetMapping
    public List<PaymentDto> getAll() {
        return paymentService.getAll();
    }
}

