package com.shrooms.scaffold.web.orders;

import com.shrooms.scaffold.exception.inspection.InspectionApiException;
import com.shrooms.scaffold.model.entity.customOrder.CustomOrder;
import com.shrooms.scaffold.model.entity.customOrder.RequestStatus;
import com.shrooms.scaffold.model.entity.order.Order;
import com.shrooms.scaffold.model.entity.order.OrderStatus;
import com.shrooms.scaffold.model.entity.order.OrderType;
import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import com.shrooms.scaffold.model.entity.user.RoleType;
import com.shrooms.scaffold.service.customOrder.CustomOrderService;
import com.shrooms.scaffold.service.inspection.InspectionIntegrationService;
import com.shrooms.scaffold.service.order.OrderService;
import com.shrooms.scaffold.service.user.UserDetailsData;
import com.shrooms.scaffold.web.OrdersController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdersController.class)
public class OrdersControllerApiTest {

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CustomOrderService customOrderService;

    @MockitoBean
    private InspectionIntegrationService inspectionIntegrationService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getOrdersPage_shouldReturnOrdersViewAndModelAttributes() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID customOrderId = UUID.randomUUID();

        UserDetailsData userDetails = UserDetailsData.builder()
                .id(userId)
                .username("ivan")
                .password("123456")
                .roleType(RoleType.USER)
                .active(true)
                .blocked(false)
                .build();

        Scaffold scaffold = Scaffold.builder()
                .name("Facade scaffold")
                .build();

        Order order = Order.builder()
                .id(orderId)
                .orderStatus(OrderStatus.PENDING)
                .scaffold(scaffold)
                .build();

        CustomOrder customOrder = CustomOrder.builder()
                .id(customOrderId)
                .projectName("Custom scaffold")
                .requestStatus(RequestStatus.PENDING)
                .orderType(OrderType.CUSTOM)
                .address("Sofia Center")
                .contactPhone("0887654321")
                .build();

        when(orderService.getOrdersByUserId(userId))
                .thenReturn(List.of(order));

        when(customOrderService.getOrdersByUserId(userId))
                .thenReturn(List.of(customOrder));

        when(inspectionIntegrationService.getInspectionsByProjectIds(any()))
                .thenReturn(Map.of());

        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.get("/orders")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                )
                        ));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("customOrders"))
                .andExpect(model().attributeExists("inspectionByProjectId"));

        verify(orderService).getOrdersByUserId(userId);
        verify(customOrderService).getOrdersByUserId(userId);
        verify(inspectionIntegrationService).getInspectionsByProjectIds(any());
    }

    @Test
    public void getOrdersPage_shouldReturnOrdersViewWithEmptyInspectionsWhenInspectionApiFails() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID customOrderId = UUID.randomUUID();

        UserDetailsData userDetails = UserDetailsData.builder()
                .id(userId)
                .username("ivan")
                .password("123456")
                .roleType(RoleType.USER)
                .active(true)
                .blocked(false)
                .build();

        Scaffold scaffold = Scaffold.builder()
                .name("Facade scaffold")
                .build();

        Order order = Order.builder()
                .id(orderId)
                .orderStatus(OrderStatus.PENDING)
                .scaffold(scaffold)
                .build();

        CustomOrder customOrder = CustomOrder.builder()
                .id(customOrderId)
                .projectName("Custom scaffold")
                .requestStatus(RequestStatus.PENDING)
                .orderType(OrderType.CUSTOM)
                .address("Sofia Center")
                .contactPhone("0887654321")
                .build();


        when(orderService.getOrdersByUserId(userId))
                .thenReturn(List.of(order));

        when(customOrderService.getOrdersByUserId(userId))
                .thenReturn(List.of(customOrder));

        when(inspectionIntegrationService.getInspectionsByProjectIds(any()))
                .thenThrow(new InspectionApiException("error"));

        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.get("/orders")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                )
                        ));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("customOrders"))
                .andExpect(model().attributeExists("inspectionByProjectId"))
                .andExpect(model().attribute("inspectionByProjectId", Map.of()));;

        verify(orderService).getOrdersByUserId(userId);
        verify(customOrderService).getOrdersByUserId(userId);
        verify(inspectionIntegrationService).getInspectionsByProjectIds(any());
    }

}
