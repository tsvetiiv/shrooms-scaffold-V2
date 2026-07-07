package com.shrooms.scaffold.service.scheduling;

import com.shrooms.scaffold.model.entity.customOrder.RequestStatus;
import com.shrooms.scaffold.model.entity.order.OrderStatus;
import com.shrooms.scaffold.repository.customRequest.CustomOrderRepository;
import com.shrooms.scaffold.repository.order.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PendingOrdersReportService {

    private final OrderRepository orderRepository;
    private final CustomOrderRepository customOrderRepository;
    private final Logger logger = LoggerFactory.getLogger(PendingOrdersReportService.class);

    public PendingOrdersReportService(OrderRepository orderRepository, CustomOrderRepository customOrderRepository) {
        this.orderRepository = orderRepository;
        this.customOrderRepository = customOrderRepository;
    }

    @Scheduled(fixedRate = 3600000)
    public void countPendingOrders() {
        int pendingOrders = orderRepository.countAllByOrderStatus(OrderStatus.PENDING);
        int pendingCustomOrders = customOrderRepository.countAllByRequestStatus(RequestStatus.PENDING);

        logger.info("Pending orders: {}, pending custom requests: {}", pendingOrders, pendingCustomOrders);
    }
}
