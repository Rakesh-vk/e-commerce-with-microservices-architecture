package com.ecommerce.NotificationService.repository;

import com.ecommerce.NotificationService.entity.Notification;
import com.ecommerce.NotificationService.entity.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {

    Optional<Notification> findByEventIdAndChannel(
            UUID eventId,
            NotificationChannel channel
    );
}