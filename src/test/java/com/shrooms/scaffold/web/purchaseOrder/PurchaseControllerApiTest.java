package com.shrooms.scaffold.web.purchaseOrder;

import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import com.shrooms.scaffold.service.order.OrderService;
import com.shrooms.scaffold.service.scaffold.ScaffoldService;
import com.shrooms.scaffold.service.user.UserService;
import com.shrooms.scaffold.web.PurchaseController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PurchaseController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PurchaseControllerApiTest {

    @MockitoBean
    OrderService  orderService;

    @MockitoBean
    private ScaffoldService scaffoldService;

    @MockitoBean
    private UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getPurchasePage_shouldReturnPurchaseViewAndModelAttributes() throws Exception {
        when(scaffoldService.findAll()).thenReturn(List.of());

        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.get("/scaffolds/purchase");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("purchase"))
                .andExpect(model().attributeExists("scaffolds"))
                .andExpect(model().attributeExists("accountClosurePending"));

        verify(scaffoldService).findAll();
    }

    @Test
    public void getPurchaseForm_shouldReturnPurchaseFormViewAndModelAttributes() throws Exception {
        UUID scaffoldId = UUID.randomUUID();

        Scaffold scaffold = Scaffold.builder()
                .id(scaffoldId)
                .name("Facade scaffold")
                .build();

        when(scaffoldService.findById(scaffoldId))
                .thenReturn(scaffold);

        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.get("/scaffolds/purchase/" + scaffoldId);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("purchase-form"))
                .andExpect(model().attributeExists("scaffold"))
                .andExpect(model().attributeExists("purchaseOrderRequest"))
                .andExpect(model().attributeExists("accountClosurePending"));

        verify(scaffoldService).findById(scaffoldId);
    }

    @Test
    public void purchaseScaffold_shouldReturnPurchaseFormWhenRequestIsInvalid() throws Exception {
        UUID scaffoldId = UUID.randomUUID();

        Scaffold scaffold = Scaffold.builder()
                .id(scaffoldId)
                .name("Facade scaffold")
                .build();
        when(scaffoldService.findById(scaffoldId))
                .thenReturn(scaffold);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/scaffolds/purchase/" + scaffoldId)
                .formField("quantity", "-1")
                .formField("address", "")
                .formField("contactPhone", "123")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("purchase-form"))
                .andExpect(model().attributeExists("scaffold"))
                .andExpect(model().attributeExists("purchaseOrderRequest"))
                .andExpect(model().attributeExists("accountClosurePending"));

        verify(orderService, never()).createPurchaseOrder(any(), any());
    }

}
