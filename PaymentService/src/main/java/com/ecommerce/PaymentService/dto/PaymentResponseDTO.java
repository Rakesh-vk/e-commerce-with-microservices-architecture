package com.ecommerce.PaymentService.dto;

import com.ecommerce.PaymentService.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponseDTO(
        UUID id,
        UUID orderId,
        UUID userId,
        BigDecimal amount,
        PaymentStatus status,
        LocalDateTime createdAt
) {}