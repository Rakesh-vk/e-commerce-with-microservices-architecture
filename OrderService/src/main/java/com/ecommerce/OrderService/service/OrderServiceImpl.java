package com.ecommerce.OrderService.service;

import com.ecommerce.OrderService.client.PaymentServiceClient;
import com.ecommerce.OrderService.client.ProductServiceClient;
import com.ecommerce.OrderService.client.dto.PaymentClientResponse;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final PaymentServiceClient paymentServiceClient;

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
    @Transactional
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

        Order saved = orderRepository.save(order);   // id generated here, real INSERT
        log.info("Order persisted as PENDING. orderId: {}", saved.getId());

        log.info("Processing payment for order id: {}, amount: {}", saved.getId(), totalAmount);
        PaymentClientResponse paymentResponse = paymentServiceClient.processPayment(
                saved.getId(), request.userId(), totalAmount
        );
        log.info("Payment response received. orderId: {}, status: {}", saved.getId(), paymentResponse.status());

        if ("SUCCESS".equals(paymentResponse.status())) {
            saved.setStatus(OrderStatus.CONFIRMED);
            log.info("Payment succeeded for order id: {}", saved.getId());
        } else {
            saved.setStatus(OrderStatus.FAILED);
            log.warn("Payment failed for order id: {}. Restoring stock.", saved.getId());

            items.forEach(item -> {
                log.info("Restoring stock for product id: {}, quantity: {}",
                        item.getProductId(), item.getQuantity());
                productServiceClient.restoreStock(item.getProductId(), item.getQuantity());
            });
        }

        Order updated = orderRepository.save(saved);  // now a normal UPDATE, id already exists — no ambiguity
        log.info("Order finalized. orderId: {}, status: {}", updated.getId(), updated.getStatus());

        return OrderMapper.toResponse(updated);
    }
}