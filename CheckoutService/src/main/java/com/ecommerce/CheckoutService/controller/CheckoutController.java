package com.ecommerce.CheckoutService.controller;


import com.ecommerce.CheckoutService.client.dto.CreateOrderRequestDTO;
import com.ecommerce.CheckoutService.client.dto.OrderItemRequestDTO;
import com.ecommerce.CheckoutService.client.dto.OrderResponseDTO;
import com.ecommerce.CheckoutService.dto.CheckoutRequestDTO;
import com.ecommerce.CheckoutService.dto.CheckoutResponseDTO;
import com.ecommerce.CheckoutService.service.CheckoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO>
    orderCreation(@Valid @RequestBody CheckoutRequestDTO requestDTO){


        OrderResponseDTO responseDTO=checkoutService.callOrder(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
