package com.ecommerce.ProductService.dto;

import com.ecommerce.ProductService.entity.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// ProductResponse — what the client gets back
@Schema(description = "Response payload containing product details")
public record ProductResponseDTO(

        @Schema(
                description = "Unique ID of the product",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @Schema(
                description = "Name of the product",
                example = "iPhone 15"
        )
        String productName,

        @Schema(
                description = "Product price",
                example = "79999.99"
        )
        BigDecimal price,

        @Schema(
                description = "Available stock quantity",
                example = "50"
        )
        int stockQty,

        @Schema(
                description = "Product category",
                example = "ELECTRONICS"
        )
        ProductCategory category,

        @Schema(
                description = "Date and time when the product was created",
                example = "2026-08-31T22:45:00"
        )
        LocalDateTime createAt

) {
}