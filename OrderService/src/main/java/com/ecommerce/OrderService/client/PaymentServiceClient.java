package com.ecommerce.OrderService.client;

import com.ecommerce.OrderService.client.dto.CreatePaymentClientRequestDTO;
import com.ecommerce.OrderService.client.dto.PaymentClientResponse;
import com.ecommerce.OrderService.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceClient {

    private final RestClient paymentServiceRestClient;

    @CircuitBreaker(name = "paymentService", fallbackMethod = "processPaymentFallback")
    public PaymentClientResponse processPayment(UUID orderId, UUID userId, BigDecimal amount) {
        log.debug("Calling PaymentService for order {}", orderId);

        PaymentClientResponse response = paymentServiceRestClient.post()
                .uri("/api/payment")
                .body(new CreatePaymentClientRequestDTO(orderId, userId, amount))
                .retrieve()
                .body(PaymentClientResponse.class);

        log.debug("Payment response: {}", response);
        return response;
    }

    private PaymentClientResponse processPaymentFallback(UUID orderId, UUID userId, BigDecimal amount, Throwable t) {
        log.error("PaymentService unavailable for order {}: {}", orderId, t.getMessage());
        throw new ServiceUnavailableException("Payment service is currently unavailable");
    }
}