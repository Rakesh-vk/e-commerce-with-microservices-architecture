package com.ecommerce.PaymentService.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;


public record CreatePaymentRequestDTO(
        UUID orderId,
        UUID userId,
        BigDecimal amount) {

}
