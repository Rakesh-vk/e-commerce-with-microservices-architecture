package com.ecommerce.NotificationService.consumer;

import com.ecommerce.NotificationService.event.OrderCreatedEvent;
import com.ecommerce.NotificationService.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "order.created",
            groupId = "notification-service"
    )
    public void consume(OrderCreatedEvent event) {

        log.info(
                "Received OrderCreatedEvent. eventId={}, orderId={}, userId={}",
                event.eventId(), event.orderId(), event.userId()
        );

        notificationService.handleOrderCreated(event);
    }
}