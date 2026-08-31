package com.ecommerce.OrderService.controller;

import com.ecommerce.OrderService.dto.CreateOrderRequestDTO;
import com.ecommerce.OrderService.dto.OrderResponseDTO;
import com.ecommerce.OrderService.entity.Order;
import com.ecommerce.OrderService.service.OrderService;
import com.ecommerce.OrderService.service.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderServiceImpl orderServiceImpl;

    @GetMapping("{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderServiceImpl.getById(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> addOrder(@RequestBody CreateOrderRequestDTO requestDTO){
        OrderResponseDTO order = orderServiceImpl.createOrder(requestDTO);

        return ResponseEntity.ok(order);
    }
}
