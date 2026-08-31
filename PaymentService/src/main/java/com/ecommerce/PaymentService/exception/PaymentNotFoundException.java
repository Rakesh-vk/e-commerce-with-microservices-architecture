package com.ecommerce.PaymentService.exception;

public class PaymentNotFoundException extends RuntimeException{
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
