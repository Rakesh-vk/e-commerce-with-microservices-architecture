package com.ecommerce.OrderService.service;

import com.ecommerce.OrderService.client.ProductServiceClient;
import com.ecommerce.OrderService.client.dto.ProductClientResponse;
import com.ecommerce.OrderService.dto.CreateOrderRequestDTO;
import com.ecommerce.OrderService.dto.OrderItemRequestDTO;
import com.ecommerce.OrderService.dto.OrderResponseDTO;
import com.ecommerce.OrderService.entity.Order;
import com.ecommerce.OrderService.entity.OrderItem;
import com.ecommerce.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID userId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
    }

    @Test
    void getById_shouldReturnOrder_whenOrderExists() {
        UUID orderId = UUID.randomUUID();

        OrderItem item = OrderItem.builder()
                .id(UUID.randomUUID())
                .productId(productId)
                .quantity(2)
                .unitPrice(new BigDecimal("499.99"))
                .build();

        Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .status(com.ecommerce.OrderService.entity.OrderStatus.PENDING)
                .totalAmount(new BigDecimal("999.98"))
                .items(List.of(item))
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderResponseDTO result = orderService.getById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.id());
        assertEquals(userId, result.userId());
        assertEquals(new BigDecimal("999.98"), result.totalAmount());
        assertEquals(1, result.items().size());
        assertEquals(productId, result.items().get(0).productId());
        verify(orderRepository).findById(orderId);
    }

    @Test
    void getById_shouldThrowOrderNotFound_whenOrderDoesNotExist() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(
                com.ecommerce.OrderService.exception.OrderNotFoundException.class,
                () -> orderService.getById(orderId)
        );

        verify(orderRepository).findById(orderId);
    }

    @Test
    void createOrder_shouldCalculateTotalAndSaveOrder() {
        ProductClientResponse product = new ProductClientResponse(
                productId,
                "Laptop",
                new BigDecimal("499.99"),
                10
        );

        CreateOrderRequestDTO request = new CreateOrderRequestDTO(
                userId,
                List.of(new OrderItemRequestDTO(productId, 2))
        );

        when(productServiceClient.getProduct(productId)).thenReturn(product);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(UUID.randomUUID());
            return order;
        });

        OrderResponseDTO result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(new BigDecimal("999.98"), result.totalAmount());
        assertEquals(1, result.items().size());
        assertEquals(new BigDecimal("499.99"), result.items().get(0).unitPrice());
        assertEquals(2, result.items().get(0).quantity());

        verify(productServiceClient).getProduct(productId);
        verify(productServiceClient).decreaseStock(productId, 2);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_shouldHandleMultipleItemsAndCalculateCorrectTotal() {
        UUID secondProductId = UUID.randomUUID();

        ProductClientResponse first = new ProductClientResponse(
                productId, "Keyboard", new BigDecimal("100.00"), 20);
        ProductClientResponse second = new ProductClientResponse(
                secondProductId, "Mouse", new BigDecimal("50.50"), 20);

        CreateOrderRequestDTO request = new CreateOrderRequestDTO(
                userId,
                List.of(
                        new OrderItemRequestDTO(productId, 2),
                        new OrderItemRequestDTO(secondProductId, 3)
                )
        );

        when(productServiceClient.getProduct(productId)).thenReturn(first);
        when(productServiceClient.getProduct(secondProductId)).thenReturn(second);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(UUID.randomUUID());
            return order;
        });

        OrderResponseDTO result = orderService.createOrder(request);

        assertEquals(new BigDecimal("351.50"), result.totalAmount());
        assertEquals(2, result.items().size());

        verify(productServiceClient).decreaseStock(productId, 2);
        verify(productServiceClient).decreaseStock(secondProductId, 3);
    }
}
