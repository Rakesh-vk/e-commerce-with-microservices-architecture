package com.ecommerce.OrderService.client;

import com.ecommerce.OrderService.client.dto.ProductClientResponse;
import com.ecommerce.OrderService.client.dto.StockUpdateRequestDTO;
import com.ecommerce.OrderService.exception.InsufficientStockException;
import com.ecommerce.OrderService.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ProductServiceClientTest {

    private MockRestServiceServer server;
    private ProductServiceClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new ProductServiceClient(builder.build());
    }

    @Test
    void getProduct_shouldReturnProduct() {
        UUID productId = UUID.randomUUID();

        server.expect(requestTo("/api/products/" + productId))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "id": "%s",
                          "productName": "Laptop",
                          "price": 999.99,
                          "stockQty": 5
                        }
                        """.formatted(productId), org.springframework.http.MediaType.APPLICATION_JSON));

        ProductClientResponse response = client.getProduct(productId);

        assertEquals(productId, response.id());
        assertEquals("Laptop", response.productName());
        assertEquals(new BigDecimal("999.99"), response.price());
        assertEquals(5, response.stockQty());

        server.verify();
    }

    @Test
    void getProduct_shouldThrowProductNotFound_whenApiReturns404() {
        UUID productId = UUID.randomUUID();

        server.expect(requestTo("/api/products/" + productId))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(ProductNotFoundException.class, () -> client.getProduct(productId));

        server.verify();
    }

    @Test
    void decreaseStock_shouldSucceed() {
        UUID productId = UUID.randomUUID();

        server.expect(requestTo("/api/products/" + productId + "/stock"))
                .andExpect(method(org.springframework.http.HttpMethod.PATCH))
                .andExpect(content().json("""
                        {"quantity":2}
                        """))
                .andRespond(withSuccess());

        assertDoesNotThrow(() -> client.decreaseStock(productId, 2));

        server.verify();
    }

    @Test
    void decreaseStock_shouldThrowProductNotFound_whenApiReturns404() {
        UUID productId = UUID.randomUUID();

        server.expect(requestTo("/api/products/" + productId + "/stock"))
                .andExpect(method(org.springframework.http.HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThrows(ProductNotFoundException.class,
                () -> client.decreaseStock(productId, 2));

        server.verify();
    }

    @Test
    void decreaseStock_shouldThrowInsufficientStock_whenApiReturns409() {
        UUID productId = UUID.randomUUID();

        server.expect(requestTo("/api/products/" + productId + "/stock"))
                .andExpect(method(org.springframework.http.HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.CONFLICT));

        assertThrows(InsufficientStockException.class,
                () -> client.decreaseStock(productId, 2));

        server.verify();
    }
}
