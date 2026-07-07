package com.shrooms.scaffold.web;

import com.shrooms.scaffold.model.dto.order.RentOrderRequest;
import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.service.order.OrderService;
import com.shrooms.scaffold.service.scaffold.ScaffoldService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/scaffolds/rent")
public class RentController {

    private final OrderService orderService;
    private final ScaffoldService scaffoldService;

    public RentController(OrderService orderService, ScaffoldService scaffoldService) {
        this.orderService = orderService;
        this.scaffoldService = scaffoldService;
    }

    @GetMapping
    public ModelAndView getRentPage() {

        ModelAndView modelAndView = new ModelAndView("rent");
        modelAndView.addObject("scaffolds", scaffoldService.findAll());

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getRentForm(@PathVariable UUID id) {

        ModelAndView modelAndView = new ModelAndView("rent-form");
        modelAndView.addObject("scaffold", scaffoldService.findById(id));
        modelAndView.addObject("rentOrderRequest", new RentOrderRequest());

        return modelAndView;
    }

    @PostMapping("/{id}")
    public ModelAndView rentScaffold(@PathVariable UUID id,
                                     @Valid @ModelAttribute("rentOrderRequest") RentOrderRequest rentOrderRequest,
                                     BindingResult bindingResult,
                                     HttpSession session) {

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("rent-form");
            modelAndView.addObject("scaffold", scaffoldService.findById(id));
            modelAndView.addObject("rentOrderRequest", rentOrderRequest);
            return modelAndView;
        }

        UserDto user = (UserDto) session.getAttribute("user");

        rentOrderRequest.setScaffoldId(id);
        try {
            orderService.createRentOrder(rentOrderRequest, user);
            return new ModelAndView("redirect:/orders");
        } catch (RuntimeException exception) {
            ModelAndView modelAndView = new ModelAndView("rent-form");
            modelAndView.addObject("scaffold", scaffoldService.findById(id));
            modelAndView.addObject("rentOrderRequest", rentOrderRequest);
            modelAndView.addObject("orderError", exception.getMessage());
            return modelAndView;
        }
    }
}
