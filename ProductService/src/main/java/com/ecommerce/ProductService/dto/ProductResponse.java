package com.ecommerce.ProductService.dto;

import com.ecommerce.ProductService.entity.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// ProductResponse — what the client gets back
public record ProductResponse(
        UUID id,
        String productName,
        BigDecimal price,
        int stockQty,
        ProductCategory category,
        LocalDateTime createAt
) {}