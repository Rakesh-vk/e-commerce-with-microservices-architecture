package com.ecommerce.OrderService.controller;

import com.ecommerce.OrderService.dto.CreateOrderRequestDTO;
import com.ecommerce.OrderService.dto.OrderResponseDTO;
import com.ecommerce.OrderService.service.OrderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order APIs", description = "APIs for creating and fetching orders")
public class OrderController {

    private final OrderServiceImpl orderServiceImpl;

    @Operation(summary = "Get order by ID", description = "Fetches order details using the given order ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order fetched successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID id) {
        log.info("Fetching order with id: {}", id);

        OrderResponseDTO order = orderServiceImpl.getById(id);

        log.info("Order fetched successfully with id: {}", id);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Create order", description = "Creates a new order with the provided order details")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid order request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> addOrder(@RequestBody CreateOrderRequestDTO requestDTO) {
        log.info("Creating new order for request: {}", requestDTO);

        OrderResponseDTO order = orderServiceImpl.createOrder(requestDTO);


        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}