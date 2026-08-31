package com.ecommerce.UserService.exception;


public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String email) {
        super("Email already registered: " + email);
    }
}
