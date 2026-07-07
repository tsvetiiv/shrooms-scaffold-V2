package com.shrooms.scaffold.service.email;

import com.shrooms.scaffold.event.CustomOrderStatusChangedEvent;
import com.shrooms.scaffold.event.OrderStatusChangedEvent;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrderStatusChangedEmail(OrderStatusChangedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(event.getEmail());
        message.setSubject("Your Shrooms order status was updated");
        message.setText("""
                Hello %s,
                
                Your order for %s is now %s.
                
                
                Thank you,
                Shrooms Scaffold Solutions
                """.formatted(
                event.getCustomerName(),
                event.getScaffoldName(),
                event.getOrderStatus()
        ));

        mailSender.send(message);
    }

    public void sendCustomOrderStatusChangedEmail(CustomOrderStatusChangedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(event.getEmail());
        message.setSubject("Your Shrooms custom request status was updated");
        message.setText("""
                Hello %s,
                
                Your custom request for %s is now %s.
                
                
                Thank you,
                Shrooms Scaffold Solutions
                """.formatted(
                event.getCustomerName(),
                event.getProjectName(),
                event.getRequestStatus()
        ));

        mailSender.send(message);
    }
}
