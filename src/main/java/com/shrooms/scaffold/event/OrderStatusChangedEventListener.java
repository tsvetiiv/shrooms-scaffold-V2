package com.shrooms.scaffold.event;

import com.shrooms.scaffold.service.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusChangedEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderStatusChangedEventListener.class);

    private final EmailService emailService;

    public OrderStatusChangedEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        try {
            emailService.sendOrderStatusChangedEmail(event);
        } catch (RuntimeException exception) {
            LOGGER.warn("Failed to send order status email: {}", exception.getMessage());
        }
    }
}
