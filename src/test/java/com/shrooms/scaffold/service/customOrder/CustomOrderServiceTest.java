package com.shrooms.scaffold.service.customOrder;

import com.shrooms.scaffold.event.CustomOrderStatusChangedEvent;
import com.shrooms.scaffold.exception.customOrder.CustomOrderManagementException;
import com.shrooms.scaffold.exception.customOrder.CustomOrderNotFoundException;
import com.shrooms.scaffold.model.dto.inspection.InspectionResponseDto;
import com.shrooms.scaffold.model.dto.order.CustomOrderRequest;
import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureStatus;
import com.shrooms.scaffold.model.entity.customOrder.CustomOrder;
import com.shrooms.scaffold.model.entity.customOrder.RequestStatus;
import com.shrooms.scaffold.model.entity.order.OrderType;
import com.shrooms.scaffold.model.entity.user.User;
import com.shrooms.scaffold.model.enums.inspection.InspectionStatus;
import com.shrooms.scaffold.model.enums.inspection.RecommendedAction;
import com.shrooms.scaffold.repository.accountClosure.AccountClosureRequestRepository;
import com.shrooms.scaffold.repository.customRequest.CustomOrderRepository;
import com.shrooms.scaffold.repository.user.UserRepository;
import com.shrooms.scaffold.service.inspection.InspectionIntegrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomOrderServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomOrderRepository customOrderRepository;

    @Mock
    private AccountClosureRequestRepository accountClosureRequestRepository;

    @Mock
    ApplicationEventPublisher publisher;

    @Mock
    private InspectionIntegrationService inspectionIntegrationService;

    @InjectMocks
    private CustomOrderService customOrderService;


    @Test
    public void getAllCustomOrders_shouldReturnAllCustomOrdersOrderedByCreatedOnDesc() {

        CustomOrder firstOrder = CustomOrder.builder().build();
        CustomOrder secondOrder = CustomOrder.builder().build();

        List<CustomOrder> orders = List.of(firstOrder, secondOrder);

        when(customOrderRepository.findAllByOrderByCreatedOnDesc())
                .thenReturn(orders);

        List<CustomOrder> result = customOrderService.getAllCustomOrders();

        assertEquals(orders, result);
        assertEquals(2, result.size());

        verify(customOrderRepository).findAllByOrderByCreatedOnDesc();
    }

    @Test
    public void getAllCustomOrders_shouldReturnEmptyListWhenNoCustomOrdersExist() {
        List<CustomOrder> orders = new ArrayList<>();

        when(customOrderRepository.findAllByOrderByCreatedOnDesc())
                .thenReturn(orders);
        List<CustomOrder> result = customOrderService.getAllCustomOrders();
        assertEquals(0, result.size());
        verify(customOrderRepository).findAllByOrderByCreatedOnDesc();
    }

    @Test
    public void createCustomOrder_shouldSaveCustomOrderWithCorrectData() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .blocked(false)
                .build();

        UserDto userDto = UserDto.builder()
                .id(userId)
                .build();

        CustomOrderRequest request = CustomOrderRequest.builder()
                .height(43.0)
                .width(12.0)
                .length(34.0)
                .address("Varna")
                .contactPhone("0888123456")
                .projectDescription("Need scaffold for testing")
                .projectName("Project name")
                .orderType(OrderType.CUSTOM)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(accountClosureRequestRepository
                .existsByUserIdAndStatus(userId, AccountClosureStatus.PENDING))
                .thenReturn(false);

        customOrderService.createCustomOrder(request, userDto);

        ArgumentCaptor<CustomOrder> captor =
                ArgumentCaptor.forClass(CustomOrder.class);

        verify(customOrderRepository).save(captor.capture());

        CustomOrder savedOrder = captor.getValue();

        assertEquals("Varna", savedOrder.getAddress());
        assertEquals("0888123456", savedOrder.getContactPhone());
        assertEquals("Project name", savedOrder.getProjectName());
        assertEquals(OrderType.CUSTOM, savedOrder.getOrderType());
        assertEquals(user, savedOrder.getUser());
    }

    @Test
    public void createCustomOrder_shouldThrowExceptionWhenStartDateIsMissingForRentOrder() {
        CustomOrderRequest request = CustomOrderRequest.builder()
                .orderType(OrderType.RENT)
                .startDate(null)
                .build();
        assertThrows(CustomOrderManagementException.class,
                () -> customOrderService.createCustomOrder(request, null));
        verify(customOrderRepository, never()).save(any());
    }

    @Test
    public void updateCustomOrder_shouldUpdateAndSaveCustomOrder() {
        UUID customOrderId = UUID.randomUUID();

        User user = User.builder()
                .email("test@test.com")
                .firstName("Ivan")
                .build();

        CustomOrder customOrder = CustomOrder.builder()
                .user(user)
                .id(customOrderId)
                .projectName("Roof scaffold")
                .requestStatus(RequestStatus.PENDING)
                .installationRequired(false)
                .build();

        when(customOrderRepository.findById(customOrderId))
                .thenReturn(Optional.of(customOrder));

        customOrderService.updateCustomOrder(
                customOrderId,
                RequestStatus.APPROVED,
                new BigDecimal("500.00")
        );

        assertEquals(RequestStatus.APPROVED, customOrder.getRequestStatus());
        assertEquals(new BigDecimal("500.00"), customOrder.getEstimatedPrice());

        verify(customOrderRepository).save(customOrder);
        verify(publisher).publishEvent(any(CustomOrderStatusChangedEvent.class));
    }

    @Test
    public void updateCustomOrder_shouldThrowExceptionWhenCustomOrderDoesNotExist() {
        UUID customOrderId = UUID.randomUUID();

        when(customOrderRepository.findById(customOrderId))
                .thenReturn(Optional.empty());

        assertThrows(CustomOrderNotFoundException.class,
                () -> customOrderService.updateCustomOrder(customOrderId, RequestStatus.APPROVED, BigDecimal.ZERO));

        verify(customOrderRepository, never()).save(any(CustomOrder.class));
        verify(publisher, never()).publishEvent(any(CustomOrderStatusChangedEvent.class));

    }

    @Test
    public void deleteFinalCustomOrder_shouldNotDeleteWhenRequestStatusIsPending() {
        UUID customOrderId = UUID.randomUUID();

        CustomOrder customOrder = CustomOrder.builder()
                .id(customOrderId)
                .requestStatus(RequestStatus.PENDING)
                .build();

        when(customOrderRepository.findById(customOrderId))
                .thenReturn(Optional.of(customOrder));

        assertThrows(CustomOrderManagementException.class, () ->
                customOrderService.deleteFinalCustomOrder(customOrderId)
        );

        verify(customOrderRepository, never())
                .delete(any(CustomOrder.class));
    }

    @Test
    public void deleteFinalCustomOrder_shouldDeleteWhenRequestStatusIsApproved() {
        UUID customOrderId = UUID.randomUUID();

        CustomOrder customOrder = CustomOrder.builder()
                .id(customOrderId)
                .requestStatus(RequestStatus.APPROVED)
                .build();

        when(customOrderRepository.findById(customOrderId))
                .thenReturn(Optional.of(customOrder));

        customOrderService.deleteFinalCustomOrder(customOrderId);

        verify(customOrderRepository).delete(customOrder);
    }

    @Test
    public void updateCustomOrder_shouldUpdateWhenInspectionReportAllowsUpdate() {
        UUID customOrderId = UUID.randomUUID();

        User user = User.builder()
                .firstName("Ivan")
                .email("ivan@mail.com")
                .build();

        CustomOrder customOrder = CustomOrder.builder()
                .id(customOrderId)
                .requestStatus(RequestStatus.PENDING)
                .installationRequired(true)
                .user(user)
                .build();

        InspectionResponseDto inspectionDto = new InspectionResponseDto();
        inspectionDto.setStatus(InspectionStatus.REPORT_SUBMITTED);
        inspectionDto.setRecommendedAction(RecommendedAction.APPROVE);

        when(customOrderRepository.findById(customOrderId))
                .thenReturn(Optional.of(customOrder));
        when(inspectionIntegrationService.getInspectionsByProjectOrderId(customOrderId))
                .thenReturn(List.of(inspectionDto));

        customOrderService.updateCustomOrder(customOrderId, RequestStatus.APPROVED, new BigDecimal("12.00"));

        assertEquals(RequestStatus.APPROVED, customOrder.getRequestStatus());
        assertEquals(new BigDecimal("12.00"), customOrder.getEstimatedPrice());

        verify(customOrderRepository).save(customOrder);
        verify(publisher).publishEvent(any(CustomOrderStatusChangedEvent.class));
    }

    @Test
    public void updateCustomOrder_shouldThrowExceptionWhenInspectionReportDoesNotAllowUpdate() {
        UUID customOrderId = UUID.randomUUID();

        User user = User.builder()
                .firstName("Ivan")
                .email("ivan@mail.com")
                .build();

        CustomOrder customOrder = CustomOrder.builder()
                .id(customOrderId)
                .requestStatus(RequestStatus.APPROVED)
                .installationRequired(true)
                .user(user)
                .build();

        when(customOrderRepository.findById(customOrderId))
                .thenReturn(Optional.of(customOrder));

        assertThrows(CustomOrderManagementException.class,
                () -> customOrderService.updateCustomOrder(customOrderId, RequestStatus.APPROVED, new BigDecimal("12.00")));

        verify(customOrderRepository, never()).save(any(CustomOrder.class));
        verify(publisher, never()).publishEvent(any(CustomOrderStatusChangedEvent.class));
    }
}
