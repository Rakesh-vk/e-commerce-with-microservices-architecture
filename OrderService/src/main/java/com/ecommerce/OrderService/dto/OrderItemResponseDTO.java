package com.ecommerce.OrderService.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Response payload for a single order item")
public record OrderItemResponseDTO(

        @Schema(
                description = "Unique ID of the order item",
                example = "b7f8c8d4-71b9-4b2a-9c84-02d0cf67f321"
        )
        UUID id,

        @Schema(
                description = "Unique ID of the ordered product",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID productId,

        @Schema(
                description = "Quantity ordered for this product",
                example = "2"
        )
        int quantity,

        @Schema(
                description = "Price of one unit at the time the order was placed",
                example = "499.99"
        )
        BigDecimal unitPrice

) {
}