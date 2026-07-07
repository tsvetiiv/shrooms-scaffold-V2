package com.shrooms.scaffold.event;

import com.shrooms.scaffold.service.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CustomOrderStatusEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomOrderStatusEventListener.class);

    private final EmailService emailService;

    public CustomOrderStatusEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void onCustomOrderStatusChanged(CustomOrderStatusChangedEvent event) {
        try {
            emailService.sendCustomOrderStatusChangedEmail(event);
        } catch (RuntimeException exception) {
            LOGGER.warn("Failed to send custom order status email: {}", exception.getMessage());
        }
    }
}