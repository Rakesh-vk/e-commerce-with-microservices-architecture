package com.ecommerce.OrderService.service;

import com.ecommerce.OrderService.dto.CreateOrderRequestDTO;
import com.ecommerce.OrderService.dto.OrderResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponseDTO getById(UUID id);
    OrderResponseDTO createOrder(CreateOrderRequestDTO request);
}
