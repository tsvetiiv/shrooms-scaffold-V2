package com.shrooms.scaffold.web;

import com.shrooms.scaffold.exception.order.OrderManagementException;
import com.shrooms.scaffold.model.dto.order.PurchaseOrderRequest;
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
@RequestMapping("/scaffolds/purchase")
public class PurchaseController {

    private final OrderService orderService;
    private final ScaffoldService scaffoldService;
    private final UserService userService;

    public PurchaseController(OrderService orderService, ScaffoldService scaffoldService, UserService userService) {
        this.orderService = orderService;
        this.scaffoldService = scaffoldService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getPurchasePage(@AuthenticationPrincipal UserDetailsData userDetails) {

        ModelAndView modelAndView = new ModelAndView("purchase");
        modelAndView.addObject("scaffolds", scaffoldService.findAll());
        addAccountClosurePending(modelAndView, userDetails);

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getPurchaseForm(@PathVariable UUID id,
                                        @AuthenticationPrincipal UserDetailsData userDetails) {
        ModelAndView modelAndView = new ModelAndView("purchase-form");
        modelAndView.addObject("scaffold", scaffoldService.findById(id));
        modelAndView.addObject("purchaseOrderRequest", new PurchaseOrderRequest());
        addAccountClosurePending(modelAndView, userDetails);

        return modelAndView;
    }

    @PostMapping("/{id}")
    public ModelAndView purchaseScaffold(@PathVariable UUID id,
                                         @Valid @ModelAttribute("purchaseOrderRequest") PurchaseOrderRequest purchaseOrderRequest,
                                         BindingResult bindingResult,
                                         @AuthenticationPrincipal UserDetailsData userDetails) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("purchase-form");
            modelAndView.addObject("scaffold", scaffoldService.findById(id));
            modelAndView.addObject("purchaseOrderRequest", purchaseOrderRequest);
            addAccountClosurePending(modelAndView, userDetails);
            return modelAndView;
        }

        UserDto user = userService.getUserById(userDetails.getId());

        purchaseOrderRequest.setScaffoldId(id);

        try {
            orderService.createPurchaseOrder(purchaseOrderRequest, user);
            return new ModelAndView("redirect:/orders");

        } catch (OrderManagementException exception) {
            ModelAndView modelAndView = new ModelAndView("purchase-form");
            modelAndView.addObject("scaffold", scaffoldService.findById(id));
            modelAndView.addObject("purchaseOrderRequest", purchaseOrderRequest);
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
