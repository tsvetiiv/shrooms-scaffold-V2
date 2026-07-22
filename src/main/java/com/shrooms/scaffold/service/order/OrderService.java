package com.shrooms.scaffold.service.order;

import com.shrooms.scaffold.exception.order.OrderManagementException;
import com.shrooms.scaffold.exception.order.OrderNotFoundException;
import com.shrooms.scaffold.exception.scaffold.ScaffoldNotFoundException;
import com.shrooms.scaffold.exception.user.UserNotFoundException;
import com.shrooms.scaffold.event.OrderStatusChangedEvent;
import com.shrooms.scaffold.model.dto.inspection.InspectionResponseDto;
import com.shrooms.scaffold.model.dto.order.PurchaseOrderRequest;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ScaffoldRepository scaffoldRepository;
    private final AccountClosureRequestRepository accountClosureRequestRepository;
    private final ApplicationEventPublisher publisher;
   private final InspectionIntegrationService inspectionIntegrationService;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ScaffoldRepository scaffoldRepository,
                        AccountClosureRequestRepository accountClosureRequestRepository,
                        ApplicationEventPublisher publisher,
                        InspectionIntegrationService inspectionIntegrationService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.scaffoldRepository = scaffoldRepository;
        this.accountClosureRequestRepository = accountClosureRequestRepository;
        this.publisher = publisher;
        this.inspectionIntegrationService = inspectionIntegrationService;
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        return orderRepository.findAllByUserIdOrderByCreatedOnDesc(userId);
    }

    public void createRentOrder(RentOrderRequest request, UserDto userDto) {
        User user = userRepository
                .findById(userDto.getId())
                .orElseThrow(UserNotFoundException::new);

        validateUserCanPlaceOrder(user);

        Scaffold scaffold = scaffoldRepository
                .findById(request.getScaffoldId())
                .orElseThrow(ScaffoldNotFoundException::new);
        if (!scaffold.isAvailable()) {
            throw new OrderManagementException("The scaffold is not available");
        }

        BigDecimal totalPrice = scaffold.getPriceForRent()
                .multiply(BigDecimal.valueOf(request.getQuantity()))
                .multiply(BigDecimal.valueOf(request.getRentalWeeks()));

        Order order = Order.builder()
                .user(user)
                .scaffold(scaffold)
                .orderStatus(OrderStatus.PENDING)
                .orderType(OrderType.RENT)
                .address(request.getAddress())
                .quantity(request.getQuantity())
                .rentalWeeks(request.getRentalWeeks())
                .createdOn(LocalDateTime.now())
                .installationRequired(request.isInstallationRequired())
                .contactPhone(request.getContactPhone())
                .totalPrice(totalPrice)
                .build();

        orderRepository.save(order);
    }

    public void createPurchaseOrder(PurchaseOrderRequest request, UserDto userDto) {
        User user = userRepository
                .findById(userDto.getId())
                .orElseThrow(UserNotFoundException::new);

        validateUserCanPlaceOrder(user);

        Scaffold scaffold = scaffoldRepository
                .findById(request.getScaffoldId())
                .orElseThrow(ScaffoldNotFoundException::new);

        if (!scaffold.isAvailable()) {
            throw new OrderManagementException("The scaffold is not available");
        }

        BigDecimal totalPrice = scaffold.getPriceForSale()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = Order.builder()
                .user(user)
                .scaffold(scaffold)
                .orderStatus(OrderStatus.PENDING)
                .orderType(OrderType.PURCHASE)
                .address(request.getAddress())
                .quantity(request.getQuantity())
                .createdOn(LocalDateTime.now())
                .installationRequired(request.isInstallationRequired())
                .contactPhone(request.getContactPhone())
                .totalPrice(totalPrice)
                .build();

        orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedOnDesc();
    }

    private void validateUserCanPlaceOrder(User user) {
        boolean hasPendingClosureRequest = accountClosureRequestRepository
                .existsByUserIdAndStatus(user.getId(), AccountClosureStatus.PENDING);

        if (user.isBlocked() || hasPendingClosureRequest) {
            throw new OrderManagementException("You cannot place new orders because you requested account closure");
        }
    }

    public void updateOrderStatus(UUID orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        validateInspectionReportAllowsOrderUpdate(order, orderStatus);

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                order.getUser().getEmail(),
                order.getUser().getFirstName(),
                order.getScaffold().getName(),
                order.getOrderStatus()
        );
        publisher.publishEvent(event);
    }

    public void deleteFinalOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if (!order.getOrderStatus().equals(OrderStatus.APPROVED) && !order.getOrderStatus().equals(OrderStatus.CANCELLED)) {
            throw new OrderManagementException("Only final orders can be deleted.");
        }

        orderRepository.delete(order);
    }

    private void validateInspectionReportAllowsOrderUpdate(Order order, OrderStatus orderStatus) {
        if (OrderStatus.APPROVED.equals(order.getOrderStatus()) ||
                OrderStatus.CANCELLED.equals(order.getOrderStatus())) {
            throw new OrderManagementException("This order has already been finalized.");
        }

        if (!order.isInstallationRequired()) {
            return;
        }

        List<InspectionResponseDto> inspection =
                inspectionIntegrationService.getInspectionsByProjectOrderId(order.getId());

        if (inspection.isEmpty()) {
            throw new OrderManagementException("Inspection report is required before updating this order.");
        }

        InspectionResponseDto inspectionReport = inspection.get(0);

        if (inspectionReport.getStatus() != InspectionStatus.REPORT_SUBMITTED) {
            throw new OrderManagementException("Inspection report must be submitted before updating this order.");
        }

        if (inspectionReport.getRecommendedAction() == RecommendedAction.REJECT &&
                orderStatus == OrderStatus.APPROVED) {
            throw new OrderManagementException("This order cannot be approved because the inspection report recommends rejection.");
        }
    }

}
