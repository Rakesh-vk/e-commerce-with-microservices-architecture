package com.ecommerce.CheckoutService.service;

import com.ecommerce.CheckoutService.client.OrderServiceClient;
import com.ecommerce.CheckoutService.client.dto.OrderResponseDTO;
import com.ecommerce.CheckoutService.dto.CheckoutRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CheckoutService {
    private final OrderServiceClient orderServiceClient;


    public OrderResponseDTO callOrder(CheckoutRequestDTO requestDTO) {


        OrderResponseDTO order = orderServiceClient.createOrder(requestDTO.items());
        return order;
    }
}
