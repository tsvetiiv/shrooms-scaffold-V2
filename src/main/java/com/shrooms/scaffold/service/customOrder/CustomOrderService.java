package com.shrooms.scaffold.service.customOrder;

import com.shrooms.scaffold.event.CustomOrderStatusChangedEvent;
import com.shrooms.scaffold.model.dto.order.CustomOrderRequest;
import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.model.entity.accountClosure.AccountClosureStatus;
import com.shrooms.scaffold.model.entity.customOrder.CustomOrder;
import com.shrooms.scaffold.model.entity.customOrder.RequestStatus;
import com.shrooms.scaffold.model.entity.order.OrderType;
import com.shrooms.scaffold.model.entity.user.User;
import com.shrooms.scaffold.repository.accountClosure.AccountClosureRequestRepository;
import com.shrooms.scaffold.repository.customRequest.CustomOrderRepository;
import com.shrooms.scaffold.repository.user.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CustomOrderService {


    private final UserRepository userRepository;
    private final CustomOrderRepository customOrderRepository;
    private final AccountClosureRequestRepository accountClosureRequestRepository;
    private final ApplicationEventPublisher publisher;

    public CustomOrderService(UserRepository userRepository,
                              CustomOrderRepository customOrderRepository,
                              AccountClosureRequestRepository accountClosureRequestRepository,
                              ApplicationEventPublisher publisher) {
        this.userRepository = userRepository;
        this.customOrderRepository = customOrderRepository;
        this.accountClosureRequestRepository = accountClosureRequestRepository;
        this.publisher = publisher;
    }

    public List<CustomOrder> getOrdersByUserId(UUID userId) {
        return customOrderRepository.findAllByUserId(userId);
    }

    public void createCustomOrder(CustomOrderRequest customRequest, UserDto userDto) {

        if (OrderType.RENT.equals(customRequest.getOrderType())) {
            if (customRequest.getStartDate() == null) {
                throw new RuntimeException("Start date is required");
            }
            if (customRequest.getEndDate() == null) {
                throw new RuntimeException("End date is required");
            }
            if (customRequest.getStartDate().isBefore(LocalDate.now())) {
                throw new RuntimeException(
                        "Start date cannot be in the past.");
            }
            if (customRequest.getEndDate().isBefore(customRequest.getStartDate())) {
                throw new RuntimeException("End date cannot be before start date.");
            }
        }

        User user = userRepository
                .findById(userDto.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasPendingClosureRequest = accountClosureRequestRepository
                .existsByUserIdAndStatus(user.getId(), AccountClosureStatus.PENDING);

        if (user.isBlocked() || hasPendingClosureRequest) {
            throw new RuntimeException("You cannot place new custom orders because you requested account closure");
        }

        CustomOrder customOrder = CustomOrder.builder()
                .user(user)
                .projectName(customRequest.getProjectName())
                .description(customRequest.getProjectDescription())
                .projectImage(customRequest.getProjectImage())
                .height(customRequest.getHeight())
                .width(customRequest.getWidth())
                .length(customRequest.getLength())
                .address(customRequest.getAddress())
                .contactPhone(customRequest.getContactPhone())
                .installationRequired(customRequest.isInstallationRequired())
                .orderType(customRequest.getOrderType())
                .startDate(customRequest.getStartDate())
                .endDate(customRequest.getEndDate())
                .requestStatus(RequestStatus.PENDING)
                .createdOn(LocalDate.now())
                .build();

        customOrderRepository.save(customOrder);
    }

    public List<CustomOrder> getAllCustomOrders() {
        return customOrderRepository.findAllByOrderByCreatedOnDesc();
    }

    public void updateCustomOrder(UUID customOrderId, RequestStatus requestStatus, BigDecimal estimatedPrice) {
        CustomOrder customOrder = customOrderRepository.findById(customOrderId)
                .orElseThrow(() -> new RuntimeException("Custom order not found"));

        if (RequestStatus.APPROVED.equals(customOrder.getRequestStatus()) ||
                RequestStatus.REJECTED.equals(customOrder.getRequestStatus())) {
            throw new RuntimeException("This custom request has already been finalized.");
        }

        customOrder.setRequestStatus(requestStatus);
        customOrder.setEstimatedPrice(RequestStatus.APPROVED.equals(requestStatus) ? estimatedPrice : null);
        customOrderRepository.save(customOrder);

        CustomOrderStatusChangedEvent event = new CustomOrderStatusChangedEvent(
                customOrder.getUser().getEmail(),
                customOrder.getUser().getFirstName(),
                customOrder.getProjectName(),
                customOrder.getRequestStatus()
        );
        publisher.publishEvent(event);
    }

    public void deleteFinalCustomOrder(UUID orderId) {
        CustomOrder finalCustomOrder = customOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!finalCustomOrder.getRequestStatus().equals(RequestStatus.APPROVED) && !finalCustomOrder.getRequestStatus().equals(RequestStatus.REJECTED)) {
            throw new RuntimeException("Only final custom orders can be deleted.");
        }

        customOrderRepository.delete(finalCustomOrder);
    }
}
