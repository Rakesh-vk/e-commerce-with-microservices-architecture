package com.ecommerce.ProductService.controller;

import com.ecommerce.ProductService.dto.ProductCreateRequestDTO;
import com.ecommerce.ProductService.dto.ProductResponseDTO;
import com.ecommerce.ProductService.dto.ProductUpdateRequestDTO;
import com.ecommerce.ProductService.dto.StockUpdateRequestDTO;
import com.ecommerce.ProductService.entity.ProductCategory;
import com.ecommerce.ProductService.exception.GlobalExceptionHandler;
import com.ecommerce.ProductService.exception.InsufficientStockException;
import com.ecommerce.ProductService.exception.ProductNotFoundException;
import com.ecommerce.ProductService.service.ProductServiceImpl;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductServiceImpl productService;

    private MockMvc mockMvc;

    private final UUID productId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        ProductController controller = new ProductController(productService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void getAllProductsShouldReturn200() throws Exception {
        ProductResponseDTO response = productResponse(productId, "iPhone 15");

        when(productService.getAllProduct()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(productId.toString()))
                .andExpect(jsonPath("$[0].productName").value("iPhone 15"))
                .andExpect(jsonPath("$[0].stockQty").value(10));
    }

    @Test
    void getProductByIdShouldReturn200WhenProductExists() throws Exception {
        when(productService.getProductById(productId))
                .thenReturn(productResponse(productId, "iPhone 15"));

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.productName").value("iPhone 15"))
                .andExpect(jsonPath("$.price").value(79999.99));
    }

    @Test
    void getProductByIdShouldReturn404WhenProductDoesNotExist() throws Exception {
        when(productService.getProductById(productId))
                .thenThrow(new ProductNotFoundException("Product not found with id: " + productId));

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found with id: " + productId));
    }

    @Test
    void getProductByIdShouldReturn400ForInvalidUuid() throws Exception {
        mockMvc.perform(get("/api/products/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProductShouldReturn201() throws Exception {
        ProductResponseDTO response = productResponse(productId, "iPhone 15");

        when(productService.addProduct(any(ProductCreateRequestDTO.class)))
                .thenReturn(response);

        String body = """
                {
                  "productName": "iPhone 15",
                  "price": 79999.99,
                  "stockQty": 10,
                  "category": "ELECTRONICS"
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.productName").value("iPhone 15"));
    }

    @Test
    void createProductShouldReturn400WhenNameIsBlank() throws Exception {
        String body = """
                {
                  "productName": "",
                  "price": 79999.99,
                  "stockQty": 10,
                  "category": "ELECTRONICS"
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProductShouldReturn400WhenPriceIsZero() throws Exception {
        String body = """
                {
                  "productName": "iPhone 15",
                  "price": 0,
                  "stockQty": 10,
                  "category": "ELECTRONICS"
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProductShouldReturn400WhenPriceIsNegative() throws Exception {
        String body = """
                {
                  "productName": "iPhone 15",
                  "price": -100,
                  "stockQty": 10,
                  "category": "ELECTRONICS"
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProductShouldReturn400WhenStockIsNegative() throws Exception {
        String body = """
                {
                  "productName": "iPhone 15",
                  "price": 79999.99,
                  "stockQty": -1,
                  "category": "ELECTRONICS"
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProductShouldReturn400WhenCategoryIsMissing() throws Exception {
        String body = """
                {
                  "productName": "iPhone 15",
                  "price": 79999.99,
                  "stockQty": 10
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProductShouldReturn200() throws Exception {
        ProductResponseDTO response = productResponse(productId, "iPhone 15 Pro");

        when(productService.updateProduct(any(ProductUpdateRequestDTO.class)))
                .thenReturn(response);

        String body = """
                {
                  "id": "%s",
                  "productName": "iPhone 15 Pro",
                  "price": 89999.99,
                  "stockQty": 20,
                  "category": "ELECTRONICS"
                }
                """.formatted(productId);

        mockMvc.perform(patch("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.productName").value("iPhone 15 Pro"));
    }

    @Test
    void updateProductShouldReturn404WhenProductDoesNotExist() throws Exception {
        when(productService.updateProduct(any(ProductUpdateRequestDTO.class)))
                .thenThrow(new ProductNotFoundException("Product not found with id: " + productId));

        String body = """
                {
                  "id": "%s",
                  "productName": "iPhone 15 Pro",
                  "price": 89999.99,
                  "stockQty": 20,
                  "category": "ELECTRONICS"
                }
                """.formatted(productId);

        mockMvc.perform(patch("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateProductShouldReturn400WhenProductIdIsMissing() throws Exception {
        String body = """
                {
                  "productName": "iPhone 15 Pro",
                  "price": 89999.99,
                  "stockQty": 20,
                  "category": "ELECTRONICS"
                }
                """;

        mockMvc.perform(patch("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void decreaseStockShouldReturn204() throws Exception {
        doNothing().when(productService).decreaseStock(eq(productId), eq(2));

        String body = """
                {
                  "quantity": 2
                }
                """;

        mockMvc.perform(patch("/api/products/{id}/stock", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
    }

    @Test
    void decreaseStockShouldReturn404WhenProductDoesNotExist() throws Exception {
        doThrow(new ProductNotFoundException("Product does not exist"))
                .when(productService).decreaseStock(eq(productId), eq(2));

        String body = """
                {
                  "quantity": 2
                }
                """;

        mockMvc.perform(patch("/api/products/{id}/stock", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void decreaseStockShouldReturn409WhenStockIsInsufficient() throws Exception {
        doThrow(new InsufficientStockException("Insufficient stock for product " + productId))
                .when(productService).decreaseStock(eq(productId), eq(20));

        String body = """
                {
                  "quantity": 20
                }
                """;

        mockMvc.perform(patch("/api/products/{id}/stock", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void decreaseStockShouldReturn400WhenQuantityIsZero() throws Exception {
        String body = """
                {
                  "quantity": 0
                }
                """;

        mockMvc.perform(patch("/api/products/{id}/stock", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void decreaseStockShouldReturn400WhenQuantityIsNegative() throws Exception {
        String body = """
                {
                  "quantity": -1
                }
                """;

        mockMvc.perform(patch("/api/products/{id}/stock", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void decreaseStockShouldReturn400WhenQuantityIsMissing() throws Exception {
        String body = "{}";

        mockMvc.perform(patch("/api/products/{id}/stock", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    private ProductResponseDTO productResponse(UUID id, String name) {
        return new ProductResponseDTO(
                id,
                name,
                BigDecimal.valueOf(79999.99),
                10,
                ProductCategory.ELECTRONICS,
                LocalDateTime.of(2026, 8, 31, 10, 0)
        );
    }
}
