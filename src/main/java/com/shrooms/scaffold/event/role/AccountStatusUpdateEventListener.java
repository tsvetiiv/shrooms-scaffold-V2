package com.shrooms.scaffold.event.role;


import com.shrooms.scaffold.service.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AccountStatusUpdateEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountStatusUpdateEventListener.class);

    private final EmailService emailService;

    public AccountStatusUpdateEventListener(EmailService emailService) {
        this.emailService = emailService;
    }


    @EventListener
    public void onAccountStatusUpdate(RoleChangedEvent event) {
        try {
            emailService.sendRoleChangedEmail(event);

        } catch (RuntimeException exception) {
            LOGGER.warn("Failed to send account status email: {}", exception.getMessage());
        }
    }
}
