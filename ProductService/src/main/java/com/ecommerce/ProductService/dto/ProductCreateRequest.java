package com.ecommerce.ProductService.dto;

import com.ecommerce.ProductService.entity.ProductCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

// ProductCreateRequest — what the client sends
public record ProductCreateRequest(
        @NotBlank String productName,
        @NotNull @Positive BigDecimal price,
        @NotNull @Min(0) int stockQty,
        @NotNull ProductCategory category
) {}