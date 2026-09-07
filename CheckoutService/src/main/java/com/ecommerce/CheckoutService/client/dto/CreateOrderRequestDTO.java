package com.ecommerce.CheckoutService.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Setter;

import java.util.List;

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