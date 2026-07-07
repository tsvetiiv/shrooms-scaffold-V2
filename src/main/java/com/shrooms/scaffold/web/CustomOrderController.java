package com.shrooms.scaffold.web;

import com.shrooms.scaffold.model.dto.order.CustomOrderRequest;
import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.service.customOrder.CustomOrderService;
import com.shrooms.scaffold.service.user.UserDetailsData;
import com.shrooms.scaffold.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final UserService userService;

    public CustomOrderController(CustomOrderService customOrderService, UserService userService) {
        this.customOrderService = customOrderService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getCustomOrderPage(@AuthenticationPrincipal UserDetailsData userDetails) {
        ModelAndView modelAndView = new ModelAndView("custom-order");
        modelAndView.addObject("customOrderRequest", new CustomOrderRequest());
        addAccountClosurePending(modelAndView, userDetails);

        return modelAndView;
    }

    @PostMapping
    public ModelAndView makeCustomOrder(@Valid @ModelAttribute("customOrderRequest") CustomOrderRequest customOrderRequest,
                                        BindingResult bindingResult,
                                        @AuthenticationPrincipal UserDetailsData userDetails) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("custom-order");
            modelAndView.addObject("customOrderRequest", customOrderRequest);
            addAccountClosurePending(modelAndView, userDetails);
            return modelAndView;
        }

        try {
            UserDto user = userService.getUserById(userDetails.getId());
            customOrderService.createCustomOrder(customOrderRequest, user);
            return new ModelAndView("redirect:/orders");

        } catch (RuntimeException exception) {
            ModelAndView modelAndView = new ModelAndView("custom-order");
            modelAndView.addObject("customOrderRequest", customOrderRequest);
            modelAndView.addObject("customOrderError", exception.getMessage());
            addAccountClosurePending(modelAndView, userDetails);
            return modelAndView;
        }
    }

    private void addAccountClosurePending(ModelAndView modelAndView, UserDetailsData userDetails) {
        modelAndView.addObject("accountClosurePending",
                userDetails != null && userService.hasPendingAccountClosureRequest(userDetails.getId()));
    }
}
