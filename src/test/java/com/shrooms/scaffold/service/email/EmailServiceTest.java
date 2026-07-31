package com.shrooms.scaffold.service.email;

import com.shrooms.scaffold.event.CustomOrderStatusChangedEvent;
import com.shrooms.scaffold.event.OrderStatusChangedEvent;
import com.shrooms.scaffold.event.accountClosure.AccountClosureStatusChangedEvent;
import com.shrooms.scaffold.event.role.RoleChangedEvent;
import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureStatus;
import com.shrooms.scaffold.model.entity.customOrder.RequestStatus;
import com.shrooms.scaffold.model.entity.order.OrderStatus;
import com.shrooms.scaffold.model.entity.user.RoleType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    public void sendOrderStatusChangedEmail_shouldSendEmail() {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                "ivan@mail.com",
                "Ivan",
                "Facade",
                OrderStatus.APPROVED
        );

        emailService.sendOrderStatusChangedEmail(event);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
   public void sendOrderStatusChangedEmail_shouldThrowExceptionWhenMailSenderFails() {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                "ivan@mail.com",
                "Ivan",
                "Facade",
                OrderStatus.APPROVED
        );

        doThrow(new RuntimeException("Mail failed"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        assertThrows(RuntimeException.class,
                () -> emailService.sendOrderStatusChangedEmail(event));
    }

    @Test
    public void sendCustomOrderStatusChangedEmail_shouldSendEmail(){
        CustomOrderStatusChangedEvent event = new CustomOrderStatusChangedEvent(
                "iva@mail.com",
                "Ivan",
                "Facade",
                RequestStatus.APPROVED
        );
        emailService.sendCustomOrderStatusChangedEmail(event);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    public void sendRoleChangedEmail_shouldSendEmail() {
        RoleChangedEvent event = new RoleChangedEvent(
                "IvanIvan",
                RoleType.ADMIN,
                "ivan@mail.com"
        );

        emailService.sendRoleChangedEmail(event);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    public void sendAccountClosureStatusChangedEmail_shouldSendEmail() {
        AccountClosureStatusChangedEvent event = new AccountClosureStatusChangedEvent(
                "IvanIvan",
                "ivan@mail.com",
                AccountClosureStatus.APPROVED,
                false
        );

        emailService.sendAccountClosureStatusChangedEmail(event);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    public void sendCustomOrderStatusChangedEmail_shouldThrowExceptionWhenMailSenderFails() {
        CustomOrderStatusChangedEvent event = new CustomOrderStatusChangedEvent(
                "ivan@mail.com",
                "Ivan",
                "Facade",
                RequestStatus.APPROVED
        );

        doThrow(new RuntimeException("Mail failed"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        assertThrows(RuntimeException.class,
                () -> emailService.sendCustomOrderStatusChangedEmail(event));
    }
}
