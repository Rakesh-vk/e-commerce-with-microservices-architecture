package com.ecommerce.NotificationService.service;

import com.ecommerce.NotificationService.entity.Notification;
import com.ecommerce.NotificationService.entity.NotificationChannel;
import com.ecommerce.NotificationService.entity.NotificationStatus;
import com.ecommerce.NotificationService.entity.NotificationType;
import com.ecommerce.NotificationService.event.OrderCreatedEvent;
import com.ecommerce.NotificationService.exception.NotificationException;
import com.ecommerce.NotificationService.producer.NotificationDeliveryException;
import com.ecommerce.NotificationService.producer.NotificationProvider;
import com.ecommerce.NotificationService.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final List<NotificationProvider> providers;

    @Override
    public void handleOrderCreated(OrderCreatedEvent event) {

        Optional<Notification> existing = notificationRepository
                .findByEventIdAndChannel(event.eventId(), NotificationChannel.EMAIL);

        if (existing.isPresent()) {
            log.info(
                    "Notification already processed for eventId={}, channel={}. Skipping.",
                    event.eventId(),
                    NotificationChannel.EMAIL
            );
            return;
        }

        Notification notification = buildNotification(event);
        notification = notificationRepository.save(notification);

        dispatch(notification);
    }

    private Notification buildNotification(OrderCreatedEvent event) {
        return Notification.builder()
                .eventId(event.eventId())
                .orderId(event.orderId())
                .userId(event.userId())
                .recipient(event.customerEmail())
                .subject("Your order has been placed")
                .content(buildOrderCreatedBody(event))
                .type(NotificationType.ORDER_CREATED)
                .channel(NotificationChannel.EMAIL)
                .status(NotificationStatus.PENDING)
                .attemptCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String buildOrderCreatedBody(OrderCreatedEvent event) {
        return "Hi,\n\n"
                + "We've received your order " + event.productName() + " "
                + "for " + event.orderAmount() + ".\n\n"
                + "Thanks for shopping with us!";
    }

    private void dispatch(Notification notification) {

        notification.setStatus(NotificationStatus.PROCESSING);
        notification.setAttemptCount(notification.getAttemptCount() + 1);
        notification.setUpdatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        NotificationProvider provider = providers.stream()
                .filter(p -> p.supports() == notification.getChannel())
                .findFirst()
                .orElseThrow(() -> new NotificationException(
                        "No provider registered for channel: " + notification.getChannel()
                ));

        try {
            provider.send(notification);

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());

        } catch (NotificationDeliveryException ex) {
            log.error(
                    "Delivery failed for notificationId={}, channel={}",
                    notification.getId(),
                    notification.getChannel(),
                    ex
            );
            notification.setStatus(NotificationStatus.FAILED);
            notification.setLastError(ex.getMessage());
            notification.setUpdatedAt(LocalDateTime.now());
        }

        notificationRepository.save(notification);
    }
}