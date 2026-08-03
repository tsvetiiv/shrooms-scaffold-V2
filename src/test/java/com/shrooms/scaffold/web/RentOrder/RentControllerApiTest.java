package com.shrooms.scaffold.web.RentOrder;

import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import com.shrooms.scaffold.service.order.OrderService;
import com.shrooms.scaffold.service.scaffold.ScaffoldService;
import com.shrooms.scaffold.service.user.UserService;
import com.shrooms.scaffold.web.RentController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(RentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RentControllerApiTest {

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private ScaffoldService scaffoldService;

    @MockitoBean
    private UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getRentPage_shouldReturnRentViewAndModelAttributes() throws Exception {
        when(scaffoldService.findAll()).thenReturn(List.of());

        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.get("/scaffolds/rent");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("rent"))
                .andExpect(model().attributeExists("scaffolds"))
                .andExpect(model().attributeExists("accountClosurePending"));

        verify(scaffoldService).findAll();
    }

    @Test
    public void getRentForm_shouldReturnRentFormViewAndModelAttributes() throws Exception {
        UUID scaffoldId = UUID.randomUUID();

        Scaffold scaffold = Scaffold.builder()
                .id(scaffoldId)
                .name("Facade scaffold")
                .build();

        when(scaffoldService.findById(scaffoldId))
                .thenReturn(scaffold);

        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.get("/scaffolds/rent/" + scaffoldId);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("rent-form"))
                .andExpect(model().attributeExists("scaffold"))
                .andExpect(model().attributeExists("rentOrderRequest"))
                .andExpect(model().attributeExists("accountClosurePending"));

        verify(scaffoldService).findById(scaffoldId);
    }

    @Test
    public void rentScaffold_shouldReturnRentFormWhenRequestIsInvalid() throws Exception {
        UUID scaffoldId = UUID.randomUUID();

        Scaffold scaffold = Scaffold.builder()
                .id(scaffoldId)
                .name("Facade scaffold")
                .build();
        when(scaffoldService.findById(scaffoldId))
                .thenReturn(scaffold);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/scaffolds/rent/" + scaffoldId)
                .formField("quantity", "-1")
                .formField("address", "")
                .formField("contactPhone", "123")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("rent-form"))
                .andExpect(model().attributeExists("scaffold"))
                .andExpect(model().attributeExists("rentOrderRequest"))
                .andExpect(model().attributeExists("accountClosurePending"));

        verify(orderService, never()).createRentOrder(any(), any());
    }

}
