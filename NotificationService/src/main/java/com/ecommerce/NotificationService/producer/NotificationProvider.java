package com.ecommerce.NotificationService.producer;

import com.ecommerce.NotificationService.entity.Notification;
import com.ecommerce.NotificationService.entity.NotificationChannel;

public interface NotificationProvider {

    void send(Notification notification) throws NotificationDeliveryException;

    NotificationChannel supports();
}