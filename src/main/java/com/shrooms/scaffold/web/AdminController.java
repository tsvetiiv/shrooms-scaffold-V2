package com.shrooms.scaffold.web;

import com.shrooms.scaffold.exception.customOrder.CustomOrderManagementException;
import com.shrooms.scaffold.exception.inspection.InspectionApiException;
import com.shrooms.scaffold.exception.order.OrderManagementException;
import com.shrooms.scaffold.model.dto.scaffold.ScaffoldRequest;
import com.shrooms.scaffold.model.dto.inspection.InspectionResponseDto;
import com.shrooms.scaffold.model.entity.customOrder.CustomOrder;
import com.shrooms.scaffold.model.entity.customOrder.RequestStatus;
import com.shrooms.scaffold.model.entity.order.Order;
import com.shrooms.scaffold.model.entity.order.OrderStatus;
import com.shrooms.scaffold.model.entity.scaffold.MaterialType;
import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import com.shrooms.scaffold.model.entity.scaffold.ScaffoldCategory;
import com.shrooms.scaffold.service.customOrder.CustomOrderService;
import com.shrooms.scaffold.service.inspection.InspectionIntegrationService;
import com.shrooms.scaffold.service.order.OrderService;
import com.shrooms.scaffold.service.scaffold.ScaffoldService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final OrderService orderService;
    private final CustomOrderService customOrderService;
    private final ScaffoldService scaffoldService;
    private final InspectionIntegrationService inspectionIntegrationService;

    public AdminController(OrderService orderService,
                           CustomOrderService customOrderService,
                           ScaffoldService scaffoldService,
                           InspectionIntegrationService inspectionIntegrationService) {
        this.orderService = orderService;
        this.customOrderService = customOrderService;
        this.scaffoldService = scaffoldService;
        this.inspectionIntegrationService = inspectionIntegrationService;
    }

    @GetMapping
    public ModelAndView getAdminDashboard() {
        return new ModelAndView("admin/dashboard");
    }

    @GetMapping("/orders")
    public ModelAndView getOrders() {
        List<Order> allOrders = orderService.getAllOrders();
        ModelAndView modelAndView = new ModelAndView("admin/orders");
        modelAndView.addObject("orders", allOrders);
        modelAndView.addObject("inspectionProjectIds", getInspectionProjectIds());
        modelAndView.addObject("inspectionByProjectId", getInspectionByProjectId(allOrders.stream()
                .map(Order::getId)
                .toList()));
        return modelAndView;
    }

    @GetMapping("/custom-orders")
    public ModelAndView getCustomOrders() {
        List<CustomOrder> allCustomOrders = customOrderService.getAllCustomOrders();
        ModelAndView modelAndView = new ModelAndView("admin/custom-orders");
        modelAndView.addObject("customOrders", allCustomOrders);
        modelAndView.addObject("inspectionProjectIds", getInspectionProjectIds());
        modelAndView.addObject("inspectionByProjectId", getInspectionByProjectId(allCustomOrders.stream()
                .map(CustomOrder::getId)
                .toList()));
        return modelAndView;
    }

    @GetMapping("/scaffolds")
    public ModelAndView getScaffolds() {
        List<Scaffold> scaffolds = scaffoldService.findAll();
        ModelAndView modelAndView = new ModelAndView("admin/scaffolds");
        modelAndView.addObject("scaffolds", scaffolds);
        return modelAndView;
    }

    @PutMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable UUID id,
                                    @RequestParam OrderStatus orderStatus,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, orderStatus);
        } catch (OrderManagementException exception) {
            redirectAttributes.addFlashAttribute("warningMessage", exception.getMessage());
        }

        return "redirect:/admin/orders";
    }

    @DeleteMapping("/orders/{id}")
    public String deleteFinalOrder(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteFinalOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Order deleted successfully.");
        } catch (OrderManagementException exception) {
            redirectAttributes.addFlashAttribute("warningMessage", exception.getMessage());
        }

        return "redirect:/admin/orders";
    }
    @PutMapping("/custom-orders/{id}")
    public String updateCustomOrder(@PathVariable UUID id,
                                    @RequestParam RequestStatus requestStatus,
                                    @RequestParam(required = false) BigDecimal estimatedPrice,
                                    RedirectAttributes redirectAttributes) {

        if (RequestStatus.APPROVED.equals(requestStatus)
                && (estimatedPrice == null || estimatedPrice.compareTo(BigDecimal.ZERO) <= 0)) {
            redirectAttributes.addFlashAttribute("priceErrorOrderId", id);
            redirectAttributes.addFlashAttribute("priceError", "Estimated price must be greater than 0 before approving.");
            return "redirect:/admin/custom-orders";
        }

        try {
            customOrderService.updateCustomOrder(id, requestStatus, estimatedPrice);
        } catch (CustomOrderManagementException exception) {
            redirectAttributes.addFlashAttribute("warningMessage", exception.getMessage());
        }

        return "redirect:/admin/custom-orders";
    }

    @DeleteMapping("/custom-orders/{id}")
    public String deleteFinalCustomOrder(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            customOrderService.deleteFinalCustomOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Custom order deleted successfully.");
        } catch (CustomOrderManagementException exception) {
            redirectAttributes.addFlashAttribute("warningMessage", exception.getMessage());
        }
        return "redirect:/admin/custom-orders";
    }

    @GetMapping("/scaffolds/{id}/edit")
    public ModelAndView getEditScaffold(@PathVariable UUID id) {
        ScaffoldRequest scaffoldRequest = scaffoldService.getScaffoldForEdit(id);

        ModelAndView modelAndView = new ModelAndView("admin/edit-scaffold");
        modelAndView.addObject("scaffoldRequest", scaffoldRequest);
        modelAndView.addObject("scaffoldId", id);
        modelAndView.addObject("materialTypes", MaterialType.values());
        modelAndView.addObject("scaffoldCategories", ScaffoldCategory.values());

        return modelAndView;
    }

    @PutMapping("/scaffolds/{id}")
    public String editScaffold(@PathVariable UUID id,
                               @Valid @ModelAttribute("scaffoldRequest") ScaffoldRequest scaffoldRequest,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("scaffoldId", id);
            model.addAttribute("materialTypes", MaterialType.values());
            model.addAttribute("scaffoldCategories", ScaffoldCategory.values());

            return "admin/edit-scaffold";
        }

        scaffoldService.editScaffold(id, scaffoldRequest);

        return "redirect:/admin/scaffolds";
    }

    @GetMapping("/scaffolds/create")
    public ModelAndView getCreateScaffold() {
        ModelAndView modelAndView = new ModelAndView("admin/create-scaffold");
        modelAndView.addObject("scaffoldRequest", new ScaffoldRequest());
        modelAndView.addObject("scaffoldCategories", ScaffoldCategory.values());
        modelAndView.addObject("materialTypes", MaterialType.values());
        return modelAndView;
    }

    @PostMapping("/scaffolds")
    public ModelAndView createScaffold(@Valid @ModelAttribute("scaffoldRequest") ScaffoldRequest scaffoldRequest,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("admin/create-scaffold");
            modelAndView.addObject("scaffoldCategories", ScaffoldCategory.values());
            modelAndView.addObject("materialTypes", MaterialType.values());
            return modelAndView;
        }

        scaffoldService.createScaffold(scaffoldRequest);

        return new ModelAndView("redirect:/admin/scaffolds");
    }

    @DeleteMapping("/scaffolds/{id}")
    public ModelAndView deleteScaffold(@PathVariable UUID id, RedirectAttributes redirectAttributes) {

        boolean deleted = scaffoldService.deleteScaffold(id);

        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Scaffold deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("warningMessage", "Scaffold has existing orders and was marked as unavailable.");
        }

        return new ModelAndView("redirect:/admin/scaffolds");
    }

    private Set<UUID> getInspectionProjectIds() {
        try {
            return inspectionIntegrationService.getInspectionProjectIds();
        } catch (InspectionApiException exception) {
            return Set.of();
        }
    }

    private Map<UUID, InspectionResponseDto> getInspectionByProjectId(List<UUID> projectIds) {
        try {
            return inspectionIntegrationService.getInspectionsByProjectIds(projectIds);
        } catch (InspectionApiException exception) {
            return Map.of();
        }
    }
}
