package com.ecommerce.PaymentService.controller;

import com.ecommerce.PaymentService.dto.CreatePaymentRequestDTO;
import com.ecommerce.PaymentService.dto.PaymentResponseDTO;
import com.ecommerce.PaymentService.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment APIs", description = "APIs for creating and fetching payment details")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Get payment by ID", description = "Fetches payment details using the given payment ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment details fetched successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentDetails(@PathVariable UUID id) {
        log.info("Fetching payment details with id: {}", id);

        PaymentResponseDTO payment = paymentService.getDetails(id);

        log.info("Payment details fetched successfully with id: {}", id);
        return ResponseEntity.ok(payment);
    }

    @Operation(summary = "Create payment", description = "Creates a new payment for an order")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Payment created successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid payment request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> savePayment(
            @Valid @RequestBody CreatePaymentRequestDTO requestDTO) {

        log.info("Creating payment for order id: {}", requestDTO.orderId());

        PaymentResponseDTO payment = paymentService.savePayment(requestDTO);

        log.info("Payment created successfully with id: {}", payment.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }
}