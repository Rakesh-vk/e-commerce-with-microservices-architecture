package com.ecommerce.OrderService.mapper;

import com.ecommerce.OrderService.dto.CreateOrderRequestDTO;
import com.ecommerce.OrderService.dto.OrderItemRequestDTO;
import com.ecommerce.OrderService.dto.OrderResponseDTO;
import com.ecommerce.OrderService.entity.Order;
import com.ecommerce.OrderService.entity.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    @Test
    void toEntity_shouldMapRequestAndSetParentOrderOnItems() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderRequestDTO request = new CreateOrderRequestDTO(
                userId,
                List.of(new OrderItemRequestDTO(productId, 2))
        );

        Order order = OrderMapper.toEntity(request);

        assertEquals(userId, order.getUserId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals(productId, order.getItems().get(0).getProductId());
        assertEquals(2, order.getItems().get(0).getQuantity());
        assertSame(order, order.getItems().get(0).getOrder());
    }

    @Test
    void toResponse_shouldMapOrderAndItems() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .status(OrderStatus.CONFIRMED)
                .totalAmount(new BigDecimal("125.50"))
                .items(List.of(
                        com.ecommerce.OrderService.entity.OrderItem.builder()
                                .id(UUID.randomUUID())
                                .productId(productId)
                                .quantity(2)
                                .unitPrice(new BigDecimal("62.75"))
                                .build()
                ))
                .build();

        OrderResponseDTO response = OrderMapper.toResponse(order);

        assertEquals(orderId, response.id());
        assertEquals(userId, response.userId());
        assertEquals(OrderStatus.CONFIRMED, response.status());
        assertEquals(new BigDecimal("125.50"), response.totalAmount());
        assertEquals(1, response.items().size());
        assertEquals(productId, response.items().get(0).productId());
        assertEquals(2, response.items().get(0).quantity());
        assertEquals(new BigDecimal("62.75"), response.items().get(0).unitPrice());
    }
}
