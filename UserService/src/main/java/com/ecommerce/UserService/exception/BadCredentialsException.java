package com.ecommerce.UserService.exception;


public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message) {

        super(message);
    }
}
