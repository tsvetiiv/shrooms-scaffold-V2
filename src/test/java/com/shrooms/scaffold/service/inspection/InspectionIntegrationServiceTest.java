package com.shrooms.scaffold.service.inspection;

import com.shrooms.scaffold.exception.customOrder.CustomOrderNotFoundException;
import com.shrooms.scaffold.exception.order.OrderNotFoundException;
import com.shrooms.scaffold.inspection.InspectionClient;
import com.shrooms.scaffold.model.dto.inspection.InspectionCreateRequestDto;
import com.shrooms.scaffold.model.dto.inspection.InspectionReportRequestDto;
import com.shrooms.scaffold.model.dto.inspection.InspectionResponseDto;
import com.shrooms.scaffold.model.entity.customOrder.CustomOrder;
import com.shrooms.scaffold.model.entity.order.Order;
import com.shrooms.scaffold.model.entity.order.OrderType;
import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import com.shrooms.scaffold.model.entity.user.User;
import com.shrooms.scaffold.repository.customRequest.CustomOrderRepository;
import com.shrooms.scaffold.repository.order.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InspectionIntegrationServiceTest {

    @Mock
    private InspectionClient inspectionClient;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomOrderRepository customOrderRepository;

    @InjectMocks
    private InspectionIntegrationService inspectionIntegrationService;

    @Test
    public void getAllInspections_shouldReturnInspections() {
        InspectionResponseDto inspection = new InspectionResponseDto();

        when(inspectionClient.getInspections())
                .thenReturn(ResponseEntity.ok(List.of(inspection)));

        List<InspectionResponseDto> result =
                inspectionIntegrationService.getAllInspections();

        assertEquals(1, result.size());
        assertEquals(inspection, result.get(0));

        verify(inspectionClient).getInspections();
    }

    @Test
    public void getInspectionsByProjectIds_shouldReturnMatchingInspections() {
        UUID projectId = UUID.randomUUID();

        InspectionResponseDto inspection = new InspectionResponseDto();
        inspection.setProjectOrderId(projectId);

        when(inspectionClient.getInspections())
                .thenReturn(ResponseEntity.ok(List.of(inspection)));

        Map<UUID, InspectionResponseDto> result =
                inspectionIntegrationService.getInspectionsByProjectIds(List.of(projectId));

        assertEquals(1, result.size());
        assertEquals(inspection, result.get(projectId));

        verify(inspectionClient).getInspections();
    }

    @Test
    public void getInspectionById_shouldReturnInspection() {
        UUID inspectionId = UUID.randomUUID();

        InspectionResponseDto inspection = new InspectionResponseDto();

        when(inspectionClient.getInspection(inspectionId))
                .thenReturn(ResponseEntity.ok(inspection));

        InspectionResponseDto result =
                inspectionIntegrationService.getInspectionById(inspectionId);

        assertEquals(inspection, result);

        verify(inspectionClient).getInspection(inspectionId);
    }

    @Test
    public void createInspection_shouldCreateInspection() {
        InspectionCreateRequestDto request = new InspectionCreateRequestDto();
        request.setProjectId(UUID.randomUUID());

        InspectionResponseDto response = new InspectionResponseDto();

        when(inspectionClient.createInspection(request))
                .thenReturn(response);

        InspectionResponseDto result =
                inspectionIntegrationService.createInspection(request);

        assertEquals(response, result);
        verify(inspectionClient).createInspection(request);
    }

    @Test
    public void requestInspectionForOrder_shouldCreateInspectionWhenInstallationRequired() {
        UUID orderId = UUID.randomUUID();

        User user = User.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .build();

        Scaffold scaffold = Scaffold.builder()
                .height(2.0)
                .length(5.0)
                .build();

        Order order = Order.builder()
                .id(orderId)
                .user(user)
                .scaffold(scaffold)
                .address("Varna")
                .quantity(3)
                .installationRequired(true)
                .build();

        InspectionResponseDto response = new InspectionResponseDto();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(inspectionClient.getInspectionsByProjectOrderId(orderId))
                .thenReturn(ResponseEntity.ok(List.of()));

        when(inspectionClient.createInspection(any(InspectionCreateRequestDto.class)))
                .thenReturn(response);

        InspectionResponseDto result =
                inspectionIntegrationService.requestInspectionForOrder(orderId);

        assertEquals(response, result);

        verify(inspectionClient).createInspection(any(InspectionCreateRequestDto.class));
    }

    @Test
    public void requestInspectionForOrder_shouldThrowExceptionWhenOrderDoesNotExist() {
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> inspectionIntegrationService.requestInspectionForOrder(orderId));

        verify(inspectionClient, never()).createInspection(any(InspectionCreateRequestDto.class));
    }

    @Test
    public void requestInspectionForCustomOrder_shouldCreateInspectionWhenInstallationRequired(){
        UUID customOrderId = UUID.randomUUID();

        User user = User.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .build();

        CustomOrder customOrder = CustomOrder.builder()
                .id(customOrderId)
                .length(12.00)
                .height(12.00)
                .orderType(OrderType.RENT)
                .installationRequired(true)
                .user(user)
                .description("Some custom scaffold")
                .address("Sofia")
                .build();

        InspectionResponseDto response = new InspectionResponseDto();

        when(customOrderRepository.findById(customOrderId))
                .thenReturn(Optional.of(customOrder));

        when(inspectionClient.getInspectionsByProjectOrderId(customOrderId))
                .thenReturn(ResponseEntity.ok(List.of()));

        when(inspectionClient.createInspection(any(InspectionCreateRequestDto.class)))
                .thenReturn(response);

        InspectionResponseDto result =
                inspectionIntegrationService.requestInspectionForCustomOrder(customOrderId);

        assertEquals(response, result);
        verify(inspectionClient).createInspection(any(InspectionCreateRequestDto.class));
    }

    @Test
    public void requestInspectionForCustomOrder_shouldThrowExceptionWhenCustomOrderDoesNotExist(){
        UUID customOrderId = UUID.randomUUID();

        when(customOrderRepository.findById(customOrderId))
                .thenReturn(Optional.empty());

        assertThrows(CustomOrderNotFoundException.class,
                () ->  inspectionIntegrationService.requestInspectionForCustomOrder(customOrderId));

        verify(inspectionClient, never()).createInspection(any(InspectionCreateRequestDto.class));
    }

    @Test
    public void submitReport_shouldSubmitInspectionReport() {
        UUID inspectionId = UUID.randomUUID();

        InspectionReportRequestDto request = new InspectionReportRequestDto();
        InspectionResponseDto response = new InspectionResponseDto();

        when(inspectionClient.submitReport(inspectionId, request))
                .thenReturn(response);

        InspectionResponseDto result =
                inspectionIntegrationService.submitReport(inspectionId, request);

        assertEquals(response, result);

        verify(inspectionClient).submitReport(inspectionId, request);
    }

    @Test
    public void submitReport_shouldThrowExceptionWhenClientFails() {
        UUID inspectionId = UUID.randomUUID();

        InspectionReportRequestDto request = new InspectionReportRequestDto();

        when(inspectionClient.submitReport(inspectionId, request))
                .thenThrow(new RuntimeException("Inspection service unavailable"));

        assertThrows(RuntimeException.class,
                () -> inspectionIntegrationService.submitReport(inspectionId, request));
    }

    @Test
    public void deleteInspection_shouldDeleteInspection() {
        UUID inspectionId = UUID.randomUUID();

        inspectionIntegrationService.deleteInspection(inspectionId);

        verify(inspectionClient).deleteInspection(inspectionId);
    }

    @Test
    public void deleteInspection_shouldThrowExceptionWhenClientFails() {
        UUID inspectionId = UUID.randomUUID();

        doThrow(new RuntimeException("Inspection service unavailable"))
                .when(inspectionClient)
                .deleteInspection(inspectionId);

        assertThrows(RuntimeException.class,
                () -> inspectionIntegrationService.deleteInspection(inspectionId));
    }

}
