// client/dto/CreatePaymentClientRequestDTO.java
package com.ecommerce.OrderService.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentClientRequestDTO(
        UUID orderId,
        UUID userId,
        BigDecimal amount
) {}