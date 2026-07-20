package com.shrooms.scaffold.web;

import com.shrooms.scaffold.Exception.inspection.InspectionApiException;
import com.shrooms.scaffold.Exception.inspection.InspectionManagementException;
import com.shrooms.scaffold.model.dto.inspection.InspectionReportRequestDto;
import com.shrooms.scaffold.model.dto.inspection.InspectionResponseDto;
import com.shrooms.scaffold.model.enums.inspection.InspectionStatus;
import com.shrooms.scaffold.service.inspection.InspectionIntegrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/inspections")
public class AdminInspectionController {

    private final InspectionIntegrationService inspectionIntegrationService;

    public AdminInspectionController(InspectionIntegrationService inspectionIntegrationService) {
        this.inspectionIntegrationService = inspectionIntegrationService;
    }

    @PostMapping("/orders/{orderId}")
    public String createInspectionRequestForOrder(@PathVariable("orderId") UUID orderId,
                                                  RedirectAttributes redirectAttributes) {

        try {
            inspectionIntegrationService.requestInspectionForOrder(orderId);
            redirectAttributes.addFlashAttribute("successMessage", "Inspection request created successfully.");
        } catch (InspectionManagementException e) {
            redirectAttributes.addFlashAttribute("warningMessage", e.getMessage());
        }

        return "redirect:/admin/orders";
    }

    @PostMapping("/custom-orders/{customOrderId}")
    public String createInspectionRequestForCustomOrder(@PathVariable("customOrderId") UUID customOrderId,
                                                        RedirectAttributes redirectAttributes) {

        try {
            inspectionIntegrationService.requestInspectionForCustomOrder(customOrderId);
            redirectAttributes.addFlashAttribute("successMessage", "Inspection request created successfully.");
        } catch (InspectionManagementException e) {
            redirectAttributes.addFlashAttribute("warningMessage", e.getMessage());
        }

        return "redirect:/admin/custom-orders";
    }

    @GetMapping
    public ModelAndView getAllInspectionRequest() {
        ModelAndView modelAndView = new ModelAndView("admin/inspections");

        try {
            List<InspectionResponseDto> inspections = inspectionIntegrationService.getAllInspections();
            modelAndView.addObject("inspections", inspections);
        } catch (InspectionApiException exception) {
            modelAndView.addObject("inspections", List.of());
        }

        return modelAndView;
    }

    @GetMapping("/{inspectionId}/report")
    public ModelAndView getInspectionReport(@PathVariable("inspectionId") UUID inspectionId,
                                            RedirectAttributes redirectAttributes) {
        try {
            InspectionResponseDto inspection = inspectionIntegrationService.getInspectionById(inspectionId);
            InspectionReportRequestDto reportRequest = new InspectionReportRequestDto();
            reportRequest.setHeightMeters(inspection.getHeightMeters());
            reportRequest.setAreaSqMeters(inspection.getAreaSqMeters());

            ModelAndView modelAndView = new ModelAndView("admin/inspection-report");
            modelAndView.addObject("inspection", inspection);
            modelAndView.addObject("reportRequest", reportRequest);

            return modelAndView;
        } catch (InspectionApiException exception) {
            redirectAttributes.addFlashAttribute("warningMessage", exception.getMessage());
            return new ModelAndView("redirect:/admin/inspections");
        }
    }

    @PostMapping("/{inspectionId}/report")
    public String submitInspectionReport(@PathVariable("inspectionId") UUID inspectionId,
                                         @Valid @ModelAttribute("reportRequest") InspectionReportRequestDto reportRequest,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        try {
            InspectionResponseDto inspection = inspectionIntegrationService.getInspectionById(inspectionId);

            if (inspection.getStatus() == InspectionStatus.REPORT_SUBMITTED) {
                redirectAttributes.addFlashAttribute("warningMessage", "Inspection report is already submitted.");
                return "redirect:/admin/inspections";
            }

            if (bindingResult.hasErrors()) {
                model.addAttribute("inspection", inspection);
                model.addAttribute("reportRequest", reportRequest);

                return "admin/inspection-report";
            }

            inspectionIntegrationService.submitReport(inspectionId, reportRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Inspection report submitted successfully.");

        } catch (InspectionApiException | InspectionManagementException exception) {
            redirectAttributes.addFlashAttribute("warningMessage", exception.getMessage());
        }

        return "redirect:/admin/inspections";
    }

    @PostMapping("/{inspectionId}/delete")
    public String deleteInspection(@PathVariable("inspectionId") UUID inspectionId,
                                   RedirectAttributes redirectAttributes) {
        try {
            inspectionIntegrationService.deleteInspection(inspectionId);
            redirectAttributes.addFlashAttribute("successMessage", "Inspection request deleted successfully.");
        } catch (InspectionManagementException e) {
            redirectAttributes.addFlashAttribute("warningMessage", e.getMessage());
        }

        return "redirect:/admin/inspections";
    }
}
