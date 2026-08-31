package com.ecommerce.OrderService.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductClientResponse(
        UUID id,
        String productName,
        BigDecimal price,
        int stockQty
) {}