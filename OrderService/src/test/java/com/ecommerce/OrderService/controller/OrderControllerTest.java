package com.ecommerce.OrderService.controller;

import com.ecommerce.OrderService.dto.CreateOrderRequestDTO;
import com.ecommerce.OrderService.dto.OrderItemRequestDTO;
import com.ecommerce.OrderService.dto.OrderResponseDTO;
import com.ecommerce.OrderService.entity.OrderStatus;
import com.ecommerce.OrderService.exception.GlobalExceptionHandler;
import com.ecommerce.OrderService.exception.OrderNotFoundException;
import com.ecommerce.OrderService.service.OrderServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderServiceImpl orderServiceImpl;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        OrderController controller = new OrderController(orderServiceImpl);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getOrderById_shouldReturn200() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        OrderResponseDTO response = new OrderResponseDTO(
                orderId,
                userId,
                OrderStatus.PENDING,
                new BigDecimal("100.00"),
                null,
                List.of()
        );

        when(orderServiceImpl.getById(orderId)).thenReturn(response);

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(100.00));

        verify(orderServiceImpl).getById(orderId);
    }

    @Test
    void getOrderById_shouldReturn404_whenOrderDoesNotExist() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderServiceImpl.getById(orderId))
                .thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(orderServiceImpl).getById(orderId);
    }

    @Test
    void addOrder_shouldReturn201() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderRequestDTO request = new CreateOrderRequestDTO(
                userId,
                List.of(new OrderItemRequestDTO(productId, 2))
        );

        OrderResponseDTO response = new OrderResponseDTO(
                orderId,
                userId,
                OrderStatus.PENDING,
                new BigDecimal("999.98"),
                null,
                List.of()
        );

        when(orderServiceImpl.createOrder(any(CreateOrderRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.totalAmount").value(999.98));

        verify(orderServiceImpl).createOrder(any(CreateOrderRequestDTO.class));
    }
}
