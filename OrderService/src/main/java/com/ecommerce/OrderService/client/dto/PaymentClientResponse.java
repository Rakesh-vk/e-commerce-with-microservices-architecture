// client/dto/PaymentClientResponse.java
package com.ecommerce.OrderService.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentClientResponse(
        UUID id,
        UUID orderId,
        UUID userId,
        BigDecimal amount,
        String status   // "SUCCESS" / "FAILED" / "PENDING"
) {}