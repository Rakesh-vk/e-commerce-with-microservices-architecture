package com.ecommerce.OrderService.mapper;

import com.ecommerce.OrderService.dto.OrderItemRequestDTO;
import com.ecommerce.OrderService.dto.OrderItemResponseDTO;
import com.ecommerce.OrderService.dto.OrderResponseDTO;
import com.ecommerce.OrderService.entity.Order;
import com.ecommerce.OrderService.entity.OrderItem;

import java.util.List;

public class OrderMapper {

    private OrderMapper() {
    }

    public static OrderItem toEntity(OrderItemRequestDTO request) {
        return OrderItem.builder()
                .productId(request.productId())
                .quantity(request.quantity())
                .build();
    }

    public static OrderResponseDTO toResponse(Order order) {
        List<OrderItemResponseDTO> items = order.getItems()
                .stream()
                .map(OrderMapper::toResponse)
                .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                items
        );
    }

    public static OrderItemResponseDTO toResponse(OrderItem item) {
        return new OrderItemResponseDTO(
                item.getId(),
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }
}