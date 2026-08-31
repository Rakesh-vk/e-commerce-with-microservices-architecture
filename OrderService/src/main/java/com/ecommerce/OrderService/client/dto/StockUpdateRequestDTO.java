package com.ecommerce.OrderService.client.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockUpdateRequestDTO(
        @NotNull
        @Min(1)
        Integer quantity
) {
}