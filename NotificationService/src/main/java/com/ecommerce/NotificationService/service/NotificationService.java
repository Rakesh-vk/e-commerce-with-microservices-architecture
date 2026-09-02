package com.ecommerce.NotificationService.service;

import com.ecommerce.NotificationService.event.OrderCreatedEvent;

public interface NotificationService {
    void handleOrderCreated(OrderCreatedEvent event);
}