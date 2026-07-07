package com.shrooms.scaffold.web;

import com.shrooms.scaffold.model.dto.order.CustomOrderRequest;
import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.service.customOrder.CustomOrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/custom-order")
public class CustomOrderController {

    private final CustomOrderService customOrderService;

    public CustomOrderController(CustomOrderService customOrderService) {
        this.customOrderService = customOrderService;
    }

    @GetMapping
    public ModelAndView getCustomOrderPage() {
        ModelAndView modelAndView = new ModelAndView("custom-order");
        modelAndView.addObject("customOrderRequest", new CustomOrderRequest());

        return modelAndView;
    }

    @PostMapping
    public ModelAndView makeCustomOrder(@Valid @ModelAttribute("customOrderRequest") CustomOrderRequest customOrderRequest,
                                        BindingResult bindingResult,
                                        HttpSession session) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("custom-order");
            modelAndView.addObject("customOrderRequest", customOrderRequest);
            return modelAndView;
        }
        UserDto user = (UserDto) session.getAttribute("user");

        try {
            customOrderService.createCustomOrder(customOrderRequest, user);
            return new ModelAndView("redirect:/orders");

        } catch (RuntimeException exception) {
            ModelAndView modelAndView = new ModelAndView("custom-order");
            modelAndView.addObject("customOrderRequest", customOrderRequest);
            modelAndView.addObject("dateError", exception.getMessage());
            return modelAndView;
        }
    }
}
