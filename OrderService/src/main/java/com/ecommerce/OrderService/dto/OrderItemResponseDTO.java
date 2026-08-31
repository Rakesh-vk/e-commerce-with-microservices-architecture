package com.ecommerce.OrderService.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponseDTO(
        UUID id,
        UUID productId,
        int quantity,
        BigDecimal unitPrice
) {
}