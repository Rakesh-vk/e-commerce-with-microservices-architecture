package com.ecommerce.ProductService.dto;

import com.ecommerce.ProductService.entity.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

// ProductCreateRequest — what the client sends
@Schema(description = "Request payload for creating a product")
public record ProductCreateRequestDTO(

        @Schema(
                description = "Name of the product",
                example = "iPhone 15",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Product name is required")
        String productName,

        @Schema(
                description = "Product price",
                example = "79999.99",
                minimum = "0.01",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        BigDecimal price,

        @Schema(
                description = "Available stock quantity",
                example = "50",
                minimum = "0",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity cannot be negative")
        int stockQty,

        @Schema(
                description = "Product category",
                example = "ELECTRONICS",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Product category is required")
        ProductCategory category

) {
}