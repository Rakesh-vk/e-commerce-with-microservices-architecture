package com.ecommerce.NotificationService.event;

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
        LocalDateTime createdAt
) {}
