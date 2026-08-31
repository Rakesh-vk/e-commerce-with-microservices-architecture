package com.ecommerce.PaymentService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request payload for creating a payment")
public record CreatePaymentRequestDTO(

        @Schema(
                description = "Unique ID of the order being paid for",
                example = "7b1f5a2e-8c3a-4c91-b6f1-4f89251d9a12",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Order id is required")
        UUID orderId,

        @Schema(
                description = "Unique ID of the user making the payment",
                example = "550e8400-e29b-41d4-a716-446655440000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "User id is required")
        UUID userId,

        @Schema(
                description = "Payment amount",
                example = "999.98",
                minimum = "0.01",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount

) {
}