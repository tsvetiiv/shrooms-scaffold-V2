package com.shrooms.scaffold.web;

import com.shrooms.scaffold.model.dto.order.RentOrderRequest;
import com.shrooms.scaffold.model.dto.user.UserDto;
import com.shrooms.scaffold.service.order.OrderService;
import com.shrooms.scaffold.service.scaffold.ScaffoldService;
import com.shrooms.scaffold.service.user.UserDetailsData;
import com.shrooms.scaffold.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final UserService userService;

    public RentController(OrderService orderService, ScaffoldService scaffoldService, UserService userService) {
        this.orderService = orderService;
        this.scaffoldService = scaffoldService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getRentPage(@AuthenticationPrincipal UserDetailsData userDetails) {

        ModelAndView modelAndView = new ModelAndView("rent");
        modelAndView.addObject("scaffolds", scaffoldService.findAll());
        addAccountClosurePending(modelAndView, userDetails);

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getRentForm(@PathVariable UUID id,
                                    @AuthenticationPrincipal UserDetailsData userDetails) {

        ModelAndView modelAndView = new ModelAndView("rent-form");
        modelAndView.addObject("scaffold", scaffoldService.findById(id));
        modelAndView.addObject("rentOrderRequest", new RentOrderRequest());
        addAccountClosurePending(modelAndView, userDetails);

        return modelAndView;
    }

    @PostMapping("/{id}")
    public ModelAndView rentScaffold(@PathVariable UUID id,
                                     @Valid @ModelAttribute("rentOrderRequest") RentOrderRequest rentOrderRequest,
                                     BindingResult bindingResult,
                                     @AuthenticationPrincipal UserDetailsData userDetails) {

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("rent-form");
            modelAndView.addObject("scaffold", scaffoldService.findById(id));
            modelAndView.addObject("rentOrderRequest", rentOrderRequest);
            addAccountClosurePending(modelAndView, userDetails);
            return modelAndView;
        }

        UserDto user = userService.getUserById(userDetails.getId());

        rentOrderRequest.setScaffoldId(id);
        try {
            orderService.createRentOrder(rentOrderRequest, user);
            return new ModelAndView("redirect:/orders");
        } catch (RuntimeException exception) {
            ModelAndView modelAndView = new ModelAndView("rent-form");
            modelAndView.addObject("scaffold", scaffoldService.findById(id));
            modelAndView.addObject("rentOrderRequest", rentOrderRequest);
            modelAndView.addObject("orderError", exception.getMessage());
            addAccountClosurePending(modelAndView, userDetails);
            return modelAndView;
        }
    }

    private void addAccountClosurePending(ModelAndView modelAndView, UserDetailsData userDetails) {
        modelAndView.addObject("accountClosurePending",
                userDetails != null && userService.hasPendingAccountClosureRequest(userDetails.getId()));
    }
}
