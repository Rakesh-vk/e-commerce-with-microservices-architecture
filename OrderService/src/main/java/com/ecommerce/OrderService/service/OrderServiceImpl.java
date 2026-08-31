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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    @Override
    public OrderResponseDTO getById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return OrderMapper.toResponse(order);
    }

    @Override
    public OrderResponseDTO createOrder(CreateOrderRequestDTO request) {

        List<OrderItem> items = request.items().stream()
                .map(itemRequest -> {
                    ProductClientResponse product = productServiceClient.getProduct(itemRequest.productId());
                    if(product== null){
                        throw new ProductNotFoundException("Invalid product");
                    }
                    if (product.stockQty() < itemRequest.quantity()) {
                        throw new InsufficientStockException(
                                "Insufficient stock for product " + product.id());
                    }
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
