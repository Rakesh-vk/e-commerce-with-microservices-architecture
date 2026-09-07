package com.ecommerce.CheckoutService.client;

import com.ecommerce.CheckoutService.client.dto.CreateOrderRequestDTO;
import com.ecommerce.CheckoutService.client.dto.OrderItemRequestDTO;
import com.ecommerce.CheckoutService.client.dto.OrderResponseDTO;
import com.ecommerce.CheckoutService.dto.CheckoutResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderServiceClient {
    private final RestClient orderServiceRestClient;



    public OrderResponseDTO createOrder(List<OrderItemRequestDTO> items) {
        OrderResponseDTO body = orderServiceRestClient.post()
                .uri("/api/orders")
                .body(new CreateOrderRequestDTO(items))
                .retrieve()
                .body(OrderResponseDTO.class);
        return body;
    }
}
