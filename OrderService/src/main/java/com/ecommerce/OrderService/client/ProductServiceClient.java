package com.ecommerce.OrderService.client;

import com.ecommerce.OrderService.client.dto.ProductClientResponse;
import com.ecommerce.OrderService.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {

    private final RestClient productServiceRestClient;

    public ProductClientResponse getProduct(UUID productId) {
        log.debug("Entered ProductServiceClient");
        try {
            ProductClientResponse response = productServiceRestClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .body(ProductClientResponse.class);

            return response;

        } catch (HttpClientErrorException.NotFound ex) {
            log.debug("Error occurred in ProductServiceClient");
            throw new ProductNotFoundException(
                    "Product not found: " + productId
            );
        }
    }
}