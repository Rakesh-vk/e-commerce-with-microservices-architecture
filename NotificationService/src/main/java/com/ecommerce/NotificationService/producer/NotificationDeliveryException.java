package com.ecommerce.NotificationService.producer;

public class NotificationDeliveryException extends Exception {

    public NotificationDeliveryException(String message) {
        super(message);
    }

    public NotificationDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}