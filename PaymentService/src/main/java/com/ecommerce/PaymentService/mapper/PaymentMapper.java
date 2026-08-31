package com.ecommerce.PaymentService.mapper;


import com.ecommerce.PaymentService.dto.CreatePaymentRequestDTO;
import com.ecommerce.PaymentService.dto.PaymentResponseDTO;
import com.ecommerce.PaymentService.entity.Payment;
import com.ecommerce.PaymentService.entity.PaymentStatus;

public class PaymentMapper {

    public static Payment toEntity(CreatePaymentRequestDTO request, PaymentStatus status) {
        return Payment.builder()
                .orderId(request.orderId())
                .amount(request.amount())
                .userId(request.userId())
                .status(status)
                .build();
    }

    public static PaymentResponseDTO toResponse(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}
