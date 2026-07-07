package com.shrooms.scaffold.event.accountClosure;

import com.shrooms.scaffold.service.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AccountClosureStatusChangedEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountClosureStatusChangedEventListener.class);

    private final EmailService emailService;

    public AccountClosureStatusChangedEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void onClosureStatusChanged(AccountClosureStatusChangedEvent event) {
        try {
            emailService.sendAccountClosureStatusChangedEmail(event);
        } catch (RuntimeException exception) {
            LOGGER.warn("Failed to send order status email: {}", exception.getMessage());
        }
    }
}
