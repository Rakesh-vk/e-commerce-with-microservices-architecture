package com.ecommerce.PaymentService.service;

import com.ecommerce.PaymentService.dto.CreatePaymentRequestDTO;
import com.ecommerce.PaymentService.dto.PaymentResponseDTO;
import com.ecommerce.PaymentService.entity.Payment;
import com.ecommerce.PaymentService.entity.PaymentStatus;
import com.ecommerce.PaymentService.exception.PaymentNotFoundException;
import com.ecommerce.PaymentService.mapper.PaymentMapper;
import com.ecommerce.PaymentService.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    private final PaymentRepository paymentRepository;
    @Override
    public PaymentResponseDTO getDetails(UUID id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() ->
                new PaymentNotFoundException("Payment does not exist")
        );
        return PaymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponseDTO savePayment(CreatePaymentRequestDTO requestDTO) {
        PaymentStatus status = processPayment(requestDTO.amount());
        Payment payment = PaymentMapper.toEntity(requestDTO, status);
        Payment saved = paymentRepository.save(payment);
        return PaymentMapper.toResponse(saved);
    }
    // Simulated payment processing — in production this would call a real
    // payment gateway (Stripe/Razorpay) and likely respond async via webhook.
    // Fails on invalid amount, otherwise ~90% success rate to simulate
    // real-world gateway flakiness.
    private PaymentStatus processPayment(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentStatus.FAILED;
        }
        return new Random().nextInt(10) < 9 ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
}
