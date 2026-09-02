package com.ecommerce.NotificationService.producer;

import com.ecommerce.NotificationService.entity.Notification;
import com.ecommerce.NotificationService.entity.NotificationChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationProvider implements NotificationProvider {

    private final JavaMailSender mailSender;

    @Override
    public void send(Notification notification) throws NotificationDeliveryException {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getRecipient());
        message.setSubject(notification.getSubject());
        message.setText(notification.getContent());

        try {
            mailSender.send(message);
            log.info(
                    "Email sent. notificationId={}, recipient={}",
                    notification.getId(),
                    notification.getRecipient()
            );
        } catch (MailException ex) {
            log.error(
                    "Failed to send email. notificationId={}, recipient={}",
                    notification.getId(),
                    notification.getRecipient(),
                    ex
            );
            throw new NotificationDeliveryException(
                    "Failed to send email to " + notification.getRecipient(),
                    ex
            );
        }
    }

    @Override
    public NotificationChannel supports() {
        return NotificationChannel.EMAIL;
    }
}