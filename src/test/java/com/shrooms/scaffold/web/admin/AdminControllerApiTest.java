package com.shrooms.scaffold.web.admin;

import com.shrooms.scaffold.model.entity.customOrder.RequestStatus;
import com.shrooms.scaffold.service.customOrder.CustomOrderService;
import com.shrooms.scaffold.service.inspection.InspectionIntegrationService;
import com.shrooms.scaffold.service.order.OrderService;
import com.shrooms.scaffold.service.scaffold.ScaffoldService;
import com.shrooms.scaffold.web.AdminController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerApiTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CustomOrderService customOrderService;

    @MockitoBean
    private ScaffoldService scaffoldService;

    @MockitoBean
    private InspectionIntegrationService inspectionIntegrationService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteFinalOrder_shouldRedirectWithSuccessMessage_whenOrderIsDeleted() throws Exception {

        UUID orderId = UUID.randomUUID();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/admin/orders/{id}", orderId)
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/orders"))
                .andExpect(flash().attribute("successMessage", "Order deleted successfully."));

        verify(orderService).deleteFinalOrder(orderId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteFinalCustomOrder_shouldRedirectWithSuccessMessage_whenOrderIsDeleted() throws Exception {

        UUID customOrderId = UUID.randomUUID();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/admin/custom-orders/{id}", customOrderId)
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/custom-orders"))
                .andExpect(flash().attribute("successMessage", "Custom order deleted successfully."));

        verify(customOrderService).deleteFinalCustomOrder(customOrderId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateCustomOrder_shouldRedirectToCustomOrders_whenUpdateIsSuccessful() throws Exception {

        UUID id = UUID.randomUUID();
        BigDecimal price = BigDecimal.valueOf(1500);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/admin/custom-orders/{id}", id)
                .param("requestStatus", "APPROVED")
                .param("estimatedPrice", "1500")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/custom-orders"));

        verify(customOrderService).updateCustomOrder(id, RequestStatus.APPROVED, price);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateCustomOrder_shouldRedirectWithPriceError_whenApprovedWithoutValidPrice() throws Exception {
        UUID id = UUID.randomUUID();
        BigDecimal price = BigDecimal.valueOf(0);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/admin/custom-orders/{id}", id)
                .param("requestStatus", "APPROVED")
                .param("estimatedPrice", "0")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/custom-orders"))
                .andExpect(flash().attribute("priceErrorOrderId", id))
                .andExpect(flash().attribute("priceError", "Estimated price must be greater than 0 before approving."));

        verify(customOrderService, never()).updateCustomOrder(id, RequestStatus.APPROVED, price);
    }
}
