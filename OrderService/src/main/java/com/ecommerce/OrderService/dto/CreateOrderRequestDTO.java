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
                description = "List of items included in the order",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        List<OrderItemRequestDTO> items
) {
}