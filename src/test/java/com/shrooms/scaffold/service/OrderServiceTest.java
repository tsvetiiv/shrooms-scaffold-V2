package com.shrooms.scaffold.service;

import com.shrooms.scaffold.exception.order.OrderManagementException;
import com.shrooms.scaffold.model.dto.order.RentOrderRequest;
import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureStatus;
import com.shrooms.scaffold.model.entity.order.Order;
import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import com.shrooms.scaffold.model.entity.user.User;
import com.shrooms.scaffold.repository.accountClosure.AccountClosureRequestRepository;
import com.shrooms.scaffold.repository.order.OrderRepository;
import com.shrooms.scaffold.repository.scaffold.ScaffoldRepository;
import com.shrooms.scaffold.repository.user.UserRepository;
import com.shrooms.scaffold.service.order.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

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



}
