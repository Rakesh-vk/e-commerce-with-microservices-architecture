package com.ecommerce.PaymentService.service;

import com.ecommerce.PaymentService.dto.CreatePaymentRequestDTO;
import com.ecommerce.PaymentService.dto.PaymentResponseDTO;
import com.ecommerce.PaymentService.entity.Payment;
import com.ecommerce.PaymentService.entity.PaymentStatus;
import com.ecommerce.PaymentService.exception.PaymentNotFoundException;
import com.ecommerce.PaymentService.mapper.PaymentMapper;
import com.ecommerce.PaymentService.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final Random random = new Random();

    @Override
    public PaymentResponseDTO getDetails(UUID id) {
        log.info("Fetching payment details for payment id: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Payment not found with id: {}", id);
                    return new PaymentNotFoundException("Payment not found with id: " + id);
                });

        log.info("Payment details fetched successfully for payment id: {}", id);
        return PaymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponseDTO savePayment(CreatePaymentRequestDTO requestDTO) {
        log.info("Processing payment for order id: {}, user id: {}, amount: {}",
                requestDTO.orderId(), requestDTO.userId(), requestDTO.amount());

        PaymentStatus status = processPayment(requestDTO.amount());

        log.info("Payment processing completed for order id: {} with status: {}",
                requestDTO.orderId(), status);

        Payment payment = PaymentMapper.toEntity(requestDTO, status);
        Payment saved = paymentRepository.save(payment);

        log.info("Payment saved successfully. paymentId: {}, orderId: {}, status: {}",
                saved.getId(), saved.getOrderId(), saved.getStatus());

        return PaymentMapper.toResponse(saved);
    }

    // Simulated payment processing — in production this would call a real
    // payment gateway (Stripe/Razorpay) and likely respond async via webhook.
    // Fails on invalid amount, otherwise ~90% success rate to simulate
    // real-world gateway flakiness.
    private PaymentStatus processPayment(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Payment failed because amount is invalid: {}", amount);
            return PaymentStatus.FAILED;
        }

        boolean paymentSuccessful = random.nextInt(10) < 9;
        return paymentSuccessful ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
}