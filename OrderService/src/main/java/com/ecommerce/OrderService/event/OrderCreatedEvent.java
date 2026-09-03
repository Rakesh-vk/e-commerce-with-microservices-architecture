package com.ecommerce.OrderService.event;

import com.ecommerce.OrderService.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        UUID userId,
        String productName,
        String customerEmail,
        BigDecimal orderAmount,
        OrderStatus status,
        LocalDateTime occurredAt
) {
}