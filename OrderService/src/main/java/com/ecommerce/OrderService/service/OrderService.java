package com.ecommerce.OrderService.service;

import com.ecommerce.OrderService.dto.CreateOrderRequestDTO;
import com.ecommerce.OrderService.dto.OrderResponseDTO;
import java.util.UUID;

public interface OrderService {
    OrderResponseDTO getById(UUID id);
    OrderResponseDTO createOrder(CreateOrderRequestDTO request);
}
