package com.ecommerce.OrderService.service;

import com.ecommerce.OrderService.client.ProductServiceClient;
import com.ecommerce.OrderService.client.dto.ProductClientResponse;
import com.ecommerce.OrderService.dto.CreateOrderRequestDTO;
import com.ecommerce.OrderService.dto.OrderResponseDTO;
import com.ecommerce.OrderService.entity.Order;
import com.ecommerce.OrderService.entity.OrderItem;
import com.ecommerce.OrderService.entity.OrderStatus;
import com.ecommerce.OrderService.exception.InsufficientStockException;
import com.ecommerce.OrderService.exception.OrderNotFoundException;
import com.ecommerce.OrderService.exception.ProductNotFoundException;
import com.ecommerce.OrderService.mapper.OrderMapper;
import com.ecommerce.OrderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    @Override
    public OrderResponseDTO getById(UUID id) {
        log.debug("getById service");
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return OrderMapper.toResponse(order);
    }

    @Override
    public OrderResponseDTO createOrder(CreateOrderRequestDTO request) {
        log.debug("createOrder service");
        List<OrderItem> items = request.items().stream()
                .map(itemRequest -> {
                    log.debug("calling productServiceClient");
                    ProductClientResponse product = productServiceClient.getProduct
                            (itemRequest.productId());
                    log.debug(product.toString());

                    productServiceClient.decreaseStock(
                            product.id(),
                            itemRequest.quantity()
                    );
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

        Order order = Order.builder()
                .userId(request.userId())
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .items(items)
                .build();

        items.forEach(item -> item.setOrder(order));

        Order saved = orderRepository.save(order);
        return OrderMapper.toResponse(saved);
    }
}
