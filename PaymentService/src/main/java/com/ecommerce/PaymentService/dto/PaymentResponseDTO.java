package com.ecommerce.PaymentService.dto;

import com.ecommerce.PaymentService.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response payload containing payment details")
public record PaymentResponseDTO(

        @Schema(
                description = "Unique ID of the payment",
                example = "9d8c7b6a-5e4f-4a3b-9c2d-1e0f9a8b7c6d"
        )
        UUID id,

        @Schema(
                description = "Unique ID of the order associated with this payment",
                example = "7b1f5a2e-8c3a-4c91-b6f1-4f89251d9a12"
        )
        UUID orderId,

        @Schema(
                description = "Unique ID of the user who made the payment",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID userId,

        @Schema(
                description = "Payment amount",
                example = "999.98"
        )
        BigDecimal amount,

        @Schema(
                description = "Current status of the payment",
                example = "SUCCESS"
        )
        PaymentStatus status,

        @Schema(
                description = "Date and time when the payment was created",
                example = "2026-08-31T22:30:00"
        )
        LocalDateTime createdAt

) {
}