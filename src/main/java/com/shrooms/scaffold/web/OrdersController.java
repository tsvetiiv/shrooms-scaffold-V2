package com.shrooms.scaffold.web;

import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.service.customOrder.CustomOrderService;
import com.shrooms.scaffold.service.order.OrderService;
import jakarta.servlet.http.HttpSession;
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
    public ModelAndView getOrdersPage(HttpSession session) {

        UserDto user = (UserDto) session.getAttribute("user");

        ModelAndView modelAndView = new ModelAndView("orders");
        modelAndView.addObject("orders", orderService.getOrdersByUserId(user.getId()));

        modelAndView.addObject(
                "customOrders",
                customOrderService.getOrdersByUserId(user.getId()));
        return modelAndView;
    }
}