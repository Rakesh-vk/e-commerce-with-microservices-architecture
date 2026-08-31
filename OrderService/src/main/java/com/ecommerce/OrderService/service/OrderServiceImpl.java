package com.ecommerce.OrderService.service;

import com.ecommerce.OrderService.client.ProductServiceClient;
import com.ecommerce.OrderService.client.dto.ProductClientResponse;
import com.ecommerce.OrderService.dto.CreateOrderRequestDTO;
import com.ecommerce.OrderService.dto.OrderResponseDTO;
import com.ecommerce.OrderService.entity.Order;
import com.ecommerce.OrderService.entity.OrderItem;
import com.ecommerce.OrderService.entity.OrderStatus;
import com.ecommerce.OrderService.exception.OrderNotFoundException;
import com.ecommerce.OrderService.mapper.OrderMapper;
import com.ecommerce.OrderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    @Override
    public OrderResponseDTO getById(UUID id) {
        log.info("Fetching order with id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order not found with id: {}", id);
                    return new OrderNotFoundException(id);
                });

        log.info("Order found with id: {}", id);
        return OrderMapper.toResponse(order);
    }

    @Override
    public OrderResponseDTO createOrder(CreateOrderRequestDTO request) {
        log.info("Creating order for user id: {}", request.userId());

        List<OrderItem> items = request.items().stream()
                .map(itemRequest -> {
                    log.info("Fetching product details for product id: {}", itemRequest.productId());

                    ProductClientResponse product = productServiceClient.getProduct(itemRequest.productId());

                    log.info("Product fetched successfully. productId: {}, price: {}",
                            product.id(), product.price());

                    log.info("Decreasing stock for product id: {}, quantity: {}",
                            product.id(), itemRequest.quantity());

                    productServiceClient.decreaseStock(
                            product.id(),
                            itemRequest.quantity()
                    );

                    log.info("Stock decreased successfully for product id: {}", product.id());

                    return OrderItem.builder()
                            .productId(product.id())
                            .quantity(itemRequest.quantity())
                            .unitPrice(product.price())
                            .build();
                })
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Calculated total order amount: {} for user id: {}", totalAmount, request.userId());

        Order order = Order.builder()
                .userId(request.userId())
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .items(items)
                .build();

        items.forEach(item -> item.setOrder(order));

        Order saved = orderRepository.save(order);

        log.info("Order created successfully. orderId: {}, userId: {}, totalAmount: {}",
                saved.getId(), saved.getUserId(), saved.getTotalAmount());

        return OrderMapper.toResponse(saved);
    }
}