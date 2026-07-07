package com.shrooms.scaffold.service.order;

import com.shrooms.scaffold.event.OrderStatusChangedEvent;
import com.shrooms.scaffold.model.dto.order.PurchaseOrderRequest;
import com.shrooms.scaffold.model.dto.order.RentOrderRequest;
import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.model.entity.order.Order;
import com.shrooms.scaffold.model.entity.order.OrderStatus;
import com.shrooms.scaffold.model.entity.order.OrderType;
import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import com.shrooms.scaffold.model.entity.user.User;
import com.shrooms.scaffold.repository.order.OrderRepository;
import com.shrooms.scaffold.repository.scaffold.ScaffoldRepository;
import com.shrooms.scaffold.repository.user.UserRepository;
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
    private final ApplicationEventPublisher publisher;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ScaffoldRepository scaffoldRepository, ApplicationEventPublisher publisher) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.scaffoldRepository = scaffoldRepository;
        this.publisher = publisher;
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        return orderRepository.findAllByUserIdOrderByCreatedOnDesc(userId);
    }

    public void createRentOrder(RentOrderRequest request, UserDto userDto) {
        User user = userRepository
                .findById(userDto.getId())
                .orElseThrow();

        Scaffold scaffold = scaffoldRepository
                .findById(request.getScaffoldId())
                .orElseThrow();
        if (!scaffold.isAvailable()) {
            throw new RuntimeException("The scaffold is not available");
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
                .orElseThrow();

        Scaffold scaffold = scaffoldRepository
                .findById(request.getScaffoldId())
                .orElseThrow();

        if (!scaffold.isAvailable()) {
            throw new RuntimeException("The scaffold is not available");
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

    public void updateOrderStatus(UUID orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus().equals(OrderStatus.APPROVED) || order.getOrderStatus().equals(OrderStatus.CANCELLED)) {
            throw new RuntimeException("Final orders cannot be updated.");
        }

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
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getOrderStatus().equals(OrderStatus.APPROVED) && !order.getOrderStatus().equals(OrderStatus.CANCELLED)) {
            throw new RuntimeException("Only final orders can be deleted.");
        }

        orderRepository.delete(order);
    }
}
