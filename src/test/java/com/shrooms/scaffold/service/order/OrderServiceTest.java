package com.shrooms.scaffold.service.order;

import com.shrooms.scaffold.event.OrderStatusChangedEvent;
import com.shrooms.scaffold.exception.order.OrderManagementException;
import com.shrooms.scaffold.model.dto.inspection.InspectionResponseDto;
import com.shrooms.scaffold.model.dto.order.RentOrderRequest;
import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureStatus;
import com.shrooms.scaffold.model.entity.order.Order;
import com.shrooms.scaffold.model.entity.order.OrderStatus;
import com.shrooms.scaffold.model.entity.order.OrderType;
import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import com.shrooms.scaffold.model.entity.user.User;
import com.shrooms.scaffold.model.enums.inspection.InspectionStatus;
import com.shrooms.scaffold.model.enums.inspection.RecommendedAction;
import com.shrooms.scaffold.repository.accountClosure.AccountClosureRequestRepository;
import com.shrooms.scaffold.repository.order.OrderRepository;
import com.shrooms.scaffold.repository.scaffold.ScaffoldRepository;
import com.shrooms.scaffold.repository.user.UserRepository;
import com.shrooms.scaffold.service.inspection.InspectionIntegrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScaffoldRepository scaffoldRepository;

    @Mock
    private AccountClosureRequestRepository accountClosureRequestRepository;

    @Mock
    ApplicationEventPublisher publisher;
    @Mock
    private InspectionIntegrationService inspectionIntegrationService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createRentOrder_shouldSaveRentOrderWhenScaffoldIsAvailable() {
        UUID userId = UUID.randomUUID();
        UUID scaffoldId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .blocked(false)
                .build();

        UserDto userDto = UserDto.builder()
                .id(userId)
                .build();

        Scaffold scaffold = Scaffold.builder()
                .id(scaffoldId)
                .available(true)
                .priceForRent(new BigDecimal("100.00"))
                .build();

        RentOrderRequest request = RentOrderRequest.builder()
                .scaffoldId(scaffoldId)
                .quantity(2)
                .rentalWeeks(3)
                .address("Varna")
                .installationRequired(true)
                .contactPhone("0888123456")
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(scaffoldRepository.findById(scaffoldId))
                .thenReturn(Optional.of(scaffold));

        when(accountClosureRequestRepository.existsByUserIdAndStatus(userId, AccountClosureStatus.PENDING))
                .thenReturn(false);

        orderService.createRentOrder(request, userDto);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    public void createRentOrder_shouldThrowExceptionWhenScaffoldIsNotAvailable(){
        UUID userId = UUID.randomUUID();
        UUID scaffoldId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .blocked(false)
                .build();

        UserDto userDto = UserDto.builder()
                .id(userId)
                .build();

        Scaffold scaffold = Scaffold.builder()
                .id(scaffoldId)
                .available(false)
                .priceForRent(new BigDecimal("100.00"))
                .build();

        RentOrderRequest request = RentOrderRequest.builder()
                .scaffoldId(scaffoldId)
                .quantity(2)
                .rentalWeeks(3)
                .address("Varna")
                .installationRequired(true)
                .contactPhone("0888123456")
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(scaffoldRepository.findById(scaffoldId))
                .thenReturn(Optional.of(scaffold));

        when(accountClosureRequestRepository.existsByUserIdAndStatus(userId, AccountClosureStatus.PENDING))
                .thenReturn(false);

        assertThrows(OrderManagementException.class,
                () -> orderService.createRentOrder(request, userDto));

        verify(orderRepository, never()).save(any(Order.class));
    }


     @Test
     public void updateOrderStatus_shouldUpdateWhenInspectionReportAllowsUpdate(){
            UUID orderId = UUID.randomUUID();
            UUID scaffoldId = UUID.randomUUID();

           User user = User.builder()
                    .firstName("Ivan")
                   .email("ivan@mail.com")
                   .build();

         Scaffold scaffold = Scaffold.builder()
                 .id(scaffoldId)
                 .name("Facade")
                 .available(false)
                 .priceForRent(new BigDecimal("100.00"))
                 .build();

            Order order = Order.builder()
                    .id(orderId)
                    .user(user)
                    .scaffold(scaffold)
                    .orderStatus(OrderStatus.PENDING)
                    .orderType(OrderType.RENT)
                    .address("Varna")
                    .installationRequired(true)
                    .contactPhone("0888123456")
                    .quantity(2)
                    .rentalWeeks(3)
                    .build();

            InspectionResponseDto inspectionDto = new InspectionResponseDto();
                inspectionDto.setStatus(InspectionStatus.REPORT_SUBMITTED);
                inspectionDto.setRecommendedAction(RecommendedAction.APPROVE);

                when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
                when(inspectionIntegrationService.getInspectionsByProjectOrderId(orderId))
                        .thenReturn(List.of(inspectionDto));

                orderService.updateOrderStatus(orderId, OrderStatus.APPROVED);

               assertEquals(OrderStatus.APPROVED, order.getOrderStatus());
                assertEquals(2, order.getQuantity());

               verify(orderRepository).save(order);
                verify(publisher).publishEvent(any(OrderStatusChangedEvent.class));
        }

}
