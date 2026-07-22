package com.shrooms.scaffold.web;

import com.shrooms.scaffold.exception.inspection.InspectionApiException;
import com.shrooms.scaffold.model.dto.inspection.InspectionResponseDto;
import com.shrooms.scaffold.model.entity.customOrder.CustomOrder;
import com.shrooms.scaffold.model.entity.order.Order;
import com.shrooms.scaffold.service.inspection.InspectionIntegrationService;
import com.shrooms.scaffold.service.customOrder.CustomOrderService;
import com.shrooms.scaffold.service.order.OrderService;
import com.shrooms.scaffold.service.user.UserDetailsData;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/orders")
public class OrdersController {

    private final OrderService orderService;
    private final CustomOrderService customOrderService;
    private final InspectionIntegrationService inspectionIntegrationService;

    public OrdersController(OrderService orderService,
                            CustomOrderService customOrderService,
                            InspectionIntegrationService inspectionIntegrationService) {
        this.orderService = orderService;
        this.customOrderService = customOrderService;
        this.inspectionIntegrationService = inspectionIntegrationService;
    }

    @GetMapping
    public ModelAndView getOrdersPage(@AuthenticationPrincipal UserDetailsData userDetails) {
        ModelAndView modelAndView = new ModelAndView("orders");
        List<Order> orders = orderService.getOrdersByUserId(userDetails.getId());
        List<CustomOrder> customOrders = customOrderService.getOrdersByUserId(userDetails.getId());

        modelAndView.addObject("orders", orders);
        modelAndView.addObject("customOrders", customOrders);
        modelAndView.addObject("inspectionByProjectId", getInspectionByProjectId(orders, customOrders));

        return modelAndView;
    }

    private Map<UUID, InspectionResponseDto> getInspectionByProjectId(List<Order> orders, List<CustomOrder> customOrders) {
        Set<UUID> projectIds = getProjectIds(orders, customOrders);

        try {
            return inspectionIntegrationService.getInspectionsByProjectIds(projectIds);
        } catch (InspectionApiException exception) {
            return Map.of();
        }
    }

    private Set<UUID> getProjectIds(List<Order> orders, List<CustomOrder> customOrders) {
        Set<UUID> projectIds = new HashSet<>();

        orders.forEach(order -> projectIds.add(order.getId()));
        customOrders.forEach(customOrder -> projectIds.add(customOrder.getId()));

        return projectIds;
    }
}
