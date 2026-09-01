package com.ecommerce.PaymentService.service;

import com.ecommerce.PaymentService.dto.CreatePaymentRequestDTO;
import com.ecommerce.PaymentService.dto.PaymentResponseDTO;
import com.ecommerce.PaymentService.entity.Payment;
import com.ecommerce.PaymentService.entity.PaymentStatus;
import com.ecommerce.PaymentService.exception.PaymentNotFoundException;
import com.ecommerce.PaymentService.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void getDetails_shouldReturnPayment_whenPaymentExists() {
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Payment payment = Payment.builder()
                .id(paymentId)
                .orderId(orderId)
                .userId(userId)
                .amount(new BigDecimal("999.98"))
                .status(PaymentStatus.SUCCESS)
                .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        PaymentResponseDTO response = paymentService.getDetails(paymentId);

        assertNotNull(response);
        assertEquals(paymentId, response.id());
        assertEquals(orderId, response.orderId());
        assertEquals(userId, response.userId());
        assertEquals(new BigDecimal("999.98"), response.amount());
        assertEquals(PaymentStatus.SUCCESS, response.status());

        verify(paymentRepository).findById(paymentId);
    }

    @Test
    void getDetails_shouldThrowException_whenPaymentDoesNotExist() {
        UUID paymentId = UUID.randomUUID();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.getDetails(paymentId)
        );

        assertEquals("Payment not found with id: " + paymentId, exception.getMessage());
        verify(paymentRepository).findById(paymentId);
    }

    @Test
    void savePayment_shouldPersistPayment_andReturnResponse() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();

        CreatePaymentRequestDTO request = new CreatePaymentRequestDTO(
                orderId,
                userId,
                new BigDecimal("499.99")
        );

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(paymentId);
            return payment;
        });

        PaymentResponseDTO response = paymentService.savePayment(request);

        assertNotNull(response);
        assertEquals(paymentId, response.id());
        assertEquals(orderId, response.orderId());
        assertEquals(userId, response.userId());
        assertEquals(new BigDecimal("499.99"), response.amount());
        assertNotNull(response.status());
        assertTrue(response.status() == PaymentStatus.SUCCESS ||
                   response.status() == PaymentStatus.FAILED);

        verify(paymentRepository).save(argThat(payment ->
                orderId.equals(payment.getOrderId())
                        && userId.equals(payment.getUserId())
                        && new BigDecimal("499.99").equals(payment.getAmount())
                        && payment.getStatus() != null
        ));
    }

    @Test
    void savePayment_shouldMarkInvalidAmountAsFailed() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CreatePaymentRequestDTO request = new CreatePaymentRequestDTO(
                orderId,
                userId,
                BigDecimal.ZERO
        );

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(UUID.randomUUID());
            return payment;
        });

        PaymentResponseDTO response = paymentService.savePayment(request);

        assertEquals(PaymentStatus.FAILED, response.status());

        verify(paymentRepository).save(argThat(payment ->
                payment.getStatus() == PaymentStatus.FAILED
        ));
    }
}
