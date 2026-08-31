package com.ecommerce.PaymentService.service;

import com.ecommerce.PaymentService.dto.CreatePaymentRequestDTO;
import com.ecommerce.PaymentService.dto.PaymentResponseDTO;

import java.util.UUID;

public interface PaymentService {

    public PaymentResponseDTO getDetails(UUID id);

    PaymentResponseDTO savePayment(CreatePaymentRequestDTO requestDTO);
}
