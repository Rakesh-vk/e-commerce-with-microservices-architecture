package com.ecommerce.OrderService.exception;

public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(String inSufficientStock) {
        super(inSufficientStock);
    }
}
