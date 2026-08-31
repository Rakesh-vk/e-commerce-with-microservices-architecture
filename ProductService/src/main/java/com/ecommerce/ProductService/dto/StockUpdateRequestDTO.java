package com.ecommerce.ProductService.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockUpdateRequestDTO(
        @NotNull
        @Min(1)
        Integer quantity
) {
}