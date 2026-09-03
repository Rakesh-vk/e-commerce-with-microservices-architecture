package com.ecommerce.OrderService.exception;

public class ServiceUnavailableException extends RuntimeException{
    public ServiceUnavailableException(String invalidProduct) {
        super(invalidProduct);
    }
}
