package com.shrooms.scaffold.web;

import com.shrooms.scaffold.model.dto.order.PurchaseOrderRequest;
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
@RequestMapping("/scaffolds/purchase")
public class PurchaseController {

    private final OrderService orderService;
    private final ScaffoldService scaffoldService;

    public PurchaseController(OrderService orderService, ScaffoldService scaffoldService) {
        this.orderService = orderService;
        this.scaffoldService = scaffoldService;
    }

    @GetMapping
    public ModelAndView getPurchasePage() {

        ModelAndView modelAndView = new ModelAndView("purchase");
        modelAndView.addObject("scaffolds", scaffoldService.findAll());

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getPurchaseForm(@PathVariable UUID id) {
        ModelAndView modelAndView = new ModelAndView("purchase-form");
        modelAndView.addObject("scaffold", scaffoldService.findById(id));
        modelAndView.addObject("purchaseOrderRequest", new PurchaseOrderRequest());

        return modelAndView;
    }

    @PostMapping("/{id}")
    public ModelAndView purchaseScaffold(@PathVariable UUID id,
                                         @Valid @ModelAttribute("purchaseOrderRequest") PurchaseOrderRequest purchaseOrderRequest,
                                         BindingResult bindingResult,
                                         HttpSession session) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("purchase-form");
            modelAndView.addObject("scaffold", scaffoldService.findById(id));
            modelAndView.addObject("purchaseOrderRequest", purchaseOrderRequest);
            return modelAndView;
        }

        UserDto user = (UserDto) session.getAttribute("user");

        purchaseOrderRequest.setScaffoldId(id);

        try {
            orderService.createPurchaseOrder(purchaseOrderRequest, user);
            return new ModelAndView("redirect:/orders");

        } catch (RuntimeException exception) {
            ModelAndView modelAndView = new ModelAndView("purchase-form");
            modelAndView.addObject("scaffold", scaffoldService.findById(id));
            modelAndView.addObject("purchaseOrderRequest", purchaseOrderRequest);
            modelAndView.addObject("orderError", exception.getMessage());
            return modelAndView;
        }
    }
}
