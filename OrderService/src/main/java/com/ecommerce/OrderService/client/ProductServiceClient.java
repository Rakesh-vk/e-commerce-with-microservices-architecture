package com.ecommerce.OrderService.client;

import com.ecommerce.OrderService.client.dto.ProductClientResponse;
import com.ecommerce.OrderService.client.dto.StockUpdateRequestDTO;
import com.ecommerce.OrderService.exception.InsufficientStockException;
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
            log.debug("krakesh entered try");

            ProductClientResponse response = productServiceRestClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .body(ProductClientResponse.class);

            log.debug("Product response: {}", response);

            return response;

        } catch (HttpClientErrorException.NotFound ex) {
            log.error("Product not found: {}", productId);
            throw new ProductNotFoundException("Product not found");
        }
    }

    public void decreaseStock(UUID productId, int quantity) {
        try {
            productServiceRestClient.patch()
                    .uri("/api/products/{id}/stock", productId)
                    .body(new StockUpdateRequestDTO(quantity))
                    .retrieve()
                    .toBodilessEntity();

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ProductNotFoundException(
                    "Product not found: " + productId
            );

        } catch (HttpClientErrorException.Conflict ex) {
            throw new InsufficientStockException(
                    "Insufficient stock for product " + productId
            );
        }
    }

    public void restoreStock(UUID productId, int quantity) {
        try {
            productServiceRestClient.patch()
                    .uri("/api/products/{id}/stock/restore", productId)
                    .body(new StockUpdateRequestDTO(quantity))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ex) {
            log.error("Failed to restore stock — product not found: {}", productId);
            // don't rethrow — this runs during failure cleanup, shouldn't mask the original payment failure
        }
    }
}