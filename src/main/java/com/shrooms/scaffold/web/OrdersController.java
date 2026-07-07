package com.shrooms.scaffold.web;

import com.shrooms.scaffold.service.customOrder.CustomOrderService;
import com.shrooms.scaffold.service.order.OrderService;
import com.shrooms.scaffold.service.user.UserDetailsData;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/orders")
public class OrdersController {

    private final OrderService orderService;
    private final CustomOrderService customOrderService;

    public OrdersController(OrderService orderService, CustomOrderService customOrderService) {
        this.orderService = orderService;
        this.customOrderService = customOrderService;
    }

    @GetMapping
    public ModelAndView getOrdersPage(@AuthenticationPrincipal UserDetailsData userDetails) {
        ModelAndView modelAndView = new ModelAndView("orders");

        modelAndView.addObject("orders",
                orderService.getOrdersByUserId(userDetails.getId()));
        modelAndView.addObject("customOrders",
                customOrderService.getOrdersByUserId(userDetails.getId()));

        return modelAndView;
    }
}