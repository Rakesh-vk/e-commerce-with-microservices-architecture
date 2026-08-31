package com.ecommerce.OrderService.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException{

    public OrderNotFoundException(UUID id) {

    }
}
