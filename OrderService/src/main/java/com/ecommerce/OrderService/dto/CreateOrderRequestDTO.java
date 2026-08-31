package com.ecommerce.OrderService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Schema(description = "Request payload for creating a new order")

public record CreateOrderRequestDTO(
        @Schema(
                description = "Unique ID of the user placing the order",
                example = "550e8400-e29b-41d4-a716-446655440000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "User id is required")
        UUID userId,

        @Schema(
                description = "List of items included in the order",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        List<OrderItemRequestDTO> items
) {
}