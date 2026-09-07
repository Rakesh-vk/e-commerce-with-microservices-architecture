package com.ecommerce.CheckoutService.dto;

import com.ecommerce.CheckoutService.client.dto.OrderItemRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CheckoutRequestDTO (
    @Valid
    @NotEmpty
    List<OrderItemRequestDTO> items){
}
