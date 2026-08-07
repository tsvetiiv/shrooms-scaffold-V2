package com.shrooms.scaffold.web.admin;

import com.shrooms.scaffold.exception.order.OrderManagementException;
import com.shrooms.scaffold.model.dto.scaffold.ScaffoldRequest;
import com.shrooms.scaffold.model.entity.customOrder.RequestStatus;
import com.shrooms.scaffold.model.entity.order.OrderStatus;
import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAdminDashboard_shouldReturnDashboardView() throws Exception {
        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.get("/admin");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getScaffolds_shouldReturnScaffoldsViewWithScaffolds() throws Exception {
        Scaffold scaffold = Scaffold.builder()
                .name("Facade scaffold")
                .build();

        when(scaffoldService.findAll()).thenReturn(List.of(scaffold));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/scaffolds"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/scaffolds"))
                .andExpect(model().attribute("scaffolds", List.of(scaffold)));

        verify(scaffoldService).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getCreateScaffold_shouldReturnCreateViewWithModelAttributes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/scaffolds/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/create-scaffold"))
                .andExpect(model().attributeExists("scaffoldRequest"))
                .andExpect(model().attributeExists("scaffoldCategories"))
                .andExpect(model().attributeExists("materialTypes"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void editScaffold_shouldRedirectToScaffolds_whenRequestIsValid() throws Exception {

        UUID scaffoldId = UUID.randomUUID();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/admin/scaffolds/{id}", scaffoldId)
                .param("name", "Facade scaffold")
                .param("description", "Strong facade scaffold for exterior building work.")
                .param("height", "10")
                .param("width", "5")
                .param("length", "8")
                .param("materialType", "STEEL")
                .param("scaffoldCategory", "FACADE")
                .param("priceForRent", "150")
                .param("priceForSale", "2500")
                .param("imageUrl", "/images/facade.png")
                .param("available", "true")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/scaffolds"));

        verify(scaffoldService).editScaffold(eq(scaffoldId), any(ScaffoldRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createScaffold_shouldRedirectToScaffolds_whenRequestIsValid() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/admin/scaffolds")
                .param("name", "Facade scaffold")
                .param("description", "Strong facade scaffold for exterior building work.")
                .param("height", "10")
                .param("width", "5")
                .param("length", "8")
                .param("materialType", "STEEL")
                .param("scaffoldCategory", "FACADE")
                .param("priceForRent", "150")
                .param("priceForSale", "2500")
                .param("imageUrl", "/images/facade.png")
                .param("available", "true")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/scaffolds"));

        verify(scaffoldService).createScaffold(any(ScaffoldRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteScaffold_shouldRedirectWithSuccessMessage_whenScaffoldIsDeleted() throws Exception {
        UUID scaffoldId = UUID.randomUUID();

        when(scaffoldService.deleteScaffold(scaffoldId)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/scaffolds/{id}", scaffoldId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/scaffolds"))
                .andExpect(flash().attribute("successMessage", "Scaffold deleted successfully"));

        verify(scaffoldService).deleteScaffold(scaffoldId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteScaffold_shouldRedirectWithWarningMessage_whenScaffoldHasOrders() throws Exception {
        UUID scaffoldId = UUID.randomUUID();

        when(scaffoldService.deleteScaffold(scaffoldId)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/scaffolds/{id}", scaffoldId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/scaffolds"))
                .andExpect(flash().attribute(
                        "warningMessage",
                        "Scaffold has existing orders and was marked as unavailable."
                ));

        verify(scaffoldService).deleteScaffold(scaffoldId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateOrderStatus_shouldRedirectToOrders_whenUpdateIsSuccessful() throws Exception {
        UUID orderId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.put("/admin/orders/{id}/status", orderId)
                        .param("orderStatus", "APPROVED")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/orders"));

        verify(orderService).updateOrderStatus(orderId, OrderStatus.APPROVED);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateOrderStatus_shouldRedirectWithWarningMessage_whenServiceThrows() throws Exception {
        UUID orderId = UUID.randomUUID();

        doThrow(new OrderManagementException("Order cannot be updated."))
                .when(orderService)
                .updateOrderStatus(orderId, OrderStatus.APPROVED);

        mockMvc.perform(MockMvcRequestBuilders.put("/admin/orders/{id}/status", orderId)
                        .param("orderStatus", "APPROVED")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/orders"))
                .andExpect(flash().attribute("warningMessage", "Order cannot be updated."));

        verify(orderService).updateOrderStatus(orderId, OrderStatus.APPROVED);
    }
}
