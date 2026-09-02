package com.ecommerce.OrderService.controller;

import com.ecommerce.OrderService.dto.CreateOrderRequestDTO;
import com.ecommerce.OrderService.dto.OrderResponseDTO;
import com.ecommerce.OrderService.service.OrderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order APIs", description = "APIs for creating and fetching orders")
public class OrderController {

    private final OrderServiceImpl orderServiceImpl;

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID requesterId = UUID.fromString(authentication.getName());
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_ADMIN"));

        log.info("Fetching order with id: {} for requester: {}", id, requesterId);

        OrderResponseDTO order = orderServiceImpl.getById(id, requesterId, isAdmin);

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
    public ResponseEntity<OrderResponseDTO> addOrder(
            @RequestBody CreateOrderRequestDTO requestDTO,
            Authentication authentication,
            HttpServletRequest request) {

        UUID userId = UUID.fromString(authentication.getName());
        String customerEmail =
                (String) request.getAttribute("customerEmail");

        log.info("Creating new order for authenticated user id: {}, request: {}", userId, requestDTO);

        OrderResponseDTO order = orderServiceImpl.createOrder(requestDTO, userId,customerEmail);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}