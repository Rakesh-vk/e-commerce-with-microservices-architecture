package com.ecommerce.OrderService.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String invalidProduct) {
        super(invalidProduct);
    }
}
