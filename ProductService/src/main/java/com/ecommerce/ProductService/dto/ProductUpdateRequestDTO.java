package com.ecommerce.ProductService.dto;

import com.ecommerce.ProductService.entity.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request payload for updating product details")
public record ProductUpdateRequestDTO(

        @Schema(
                description = "Unique ID of the product to update",
                example = "550e8400-e29b-41d4-a716-446655440000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Product id is required")
        UUID id,

        @Schema(
                description = "Updated product name",
                example = "iPhone 15 Pro",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Product name is required")
        String productName,

        @Schema(
                description = "Updated product price",
                example = "89999.99",
                minimum = "0.01",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        BigDecimal price,

        @Schema(
                description = "Updated stock quantity",
                example = "75",
                minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Min(value = 0, message = "Stock quantity cannot be negative")
        int stockQty,

        @Schema(
                description = "Updated product category",
                example = "ELECTRONICS",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Product category is required")
        ProductCategory category

) {
}