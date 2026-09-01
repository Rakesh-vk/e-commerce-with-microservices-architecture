package com.ecommerce.OrderService.entity;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Current status of an order")
public enum OrderStatus {
    PENDING, CONFIRMED, CANCELLED, FAILED
}
