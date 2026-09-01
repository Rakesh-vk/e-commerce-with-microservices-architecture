package com.ecommerce.PaymentService.mapper;

import com.ecommerce.PaymentService.dto.CreatePaymentRequestDTO;
import com.ecommerce.PaymentService.dto.PaymentResponseDTO;
import com.ecommerce.PaymentService.entity.Payment;
import com.ecommerce.PaymentService.entity.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMapperTest {

    @Test
    void toEntity_shouldMapRequestAndStatus() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CreatePaymentRequestDTO request = new CreatePaymentRequestDTO(
                orderId,
                userId,
                new BigDecimal("1250.50")
        );

        Payment payment = PaymentMapper.toEntity(request, PaymentStatus.SUCCESS);

        assertNotNull(payment);
        assertEquals(orderId, payment.getOrderId());
        assertEquals(userId, payment.getUserId());
        assertEquals(new BigDecimal("1250.50"), payment.getAmount());
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertNull(payment.getId());
        assertNull(payment.getCreatedAt());
    }

    @Test
    void toResponse_shouldMapEntity() {
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        Payment payment = Payment.builder()
                .id(paymentId)
                .orderId(orderId)
                .userId(userId)
                .amount(new BigDecimal("250.75"))
                .status(PaymentStatus.FAILED)
                .createdAt(createdAt)
                .build();

        PaymentResponseDTO response = PaymentMapper.toResponse(payment);

        assertNotNull(response);
        assertEquals(paymentId, response.id());
        assertEquals(orderId, response.orderId());
        assertEquals(userId, response.userId());
        assertEquals(new BigDecimal("250.75"), response.amount());
        assertEquals(PaymentStatus.FAILED, response.status());
        assertEquals(createdAt, response.createdAt());
    }
}
