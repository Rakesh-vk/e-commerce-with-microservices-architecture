package com.ecommerce.OrderService.dto;

import com.ecommerce.OrderService.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Response payload containing order details")
public record OrderResponseDTO(

        @Schema(
                description = "Unique ID of the order",
                example = "7b1f5a2e-8c3a-4c91-b6f1-4f89251d9a12"
        )
        UUID id,

        @Schema(
                description = "Unique ID of the user who placed the order",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID userId,

        @Schema(
                description = "Current status of the order",
                example = "CREATED"
        )
        OrderStatus status,

        @Schema(
                description = "Total amount for the order",
                example = "999.98"
        )
        BigDecimal totalAmount,

        @Schema(
                description = "Date and time when the order was created",
                example = "2026-08-31T21:30:00"
        )
        LocalDateTime createdAt,

        @Schema(description = "List of items included in the order")
        List<OrderItemResponseDTO> items

) {
}