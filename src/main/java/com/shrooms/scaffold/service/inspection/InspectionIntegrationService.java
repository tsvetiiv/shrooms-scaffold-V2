package com.shrooms.scaffold.service.inspection;

import com.shrooms.scaffold.Exception.customOrder.CustomOrderNotFoundException;
import com.shrooms.scaffold.Exception.inspection.InspectionApiException;
import com.shrooms.scaffold.Exception.inspection.InspectionManagementException;
import com.shrooms.scaffold.Exception.order.OrderNotFoundException;
import com.shrooms.scaffold.inspection.InspectionClient;
import com.shrooms.scaffold.model.dto.inspection.InspectionCreateRequestDto;
import com.shrooms.scaffold.model.dto.inspection.InspectionReportRequestDto;
import com.shrooms.scaffold.model.dto.inspection.InspectionResponseDto;
import com.shrooms.scaffold.model.entity.customOrder.CustomOrder;
import com.shrooms.scaffold.model.entity.order.Order;
import com.shrooms.scaffold.model.enums.inspection.ProjectType;
import com.shrooms.scaffold.repository.customRequest.CustomOrderRepository;
import com.shrooms.scaffold.repository.order.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InspectionIntegrationService {

    private final InspectionClient inspectionClient;
    private final OrderRepository orderRepository;
    private final CustomOrderRepository customOrderRepository;

    public InspectionIntegrationService(InspectionClient inspectionClient,
                                        OrderRepository orderRepository,
                                        CustomOrderRepository customOrderRepository) {
        this.inspectionClient = inspectionClient;
        this.orderRepository = orderRepository;
        this.customOrderRepository = customOrderRepository;
    }

    public List<InspectionResponseDto> getAllInspections() {
        try {
            return inspectionClient.getInspections().getBody();
        } catch (RuntimeException exception) {
            throw new InspectionApiException("Inspection data could not be loaded right now.");
        }
    }

    public Set<UUID> getInspectionProjectIds() {
        List<InspectionResponseDto> inspections = getAllInspections();

        if (inspections == null) {
            return Set.of();
        }

        return inspections.stream()
                .map(InspectionResponseDto::getProjectOrderId)
                .collect(Collectors.toSet());
    }

    public Map<UUID, InspectionResponseDto> getInspectionsByProjectIds(Collection<UUID> projectIds) {
        List<InspectionResponseDto> inspections = getAllInspections();

        if (inspections == null || projectIds == null || projectIds.isEmpty()) {
            return Map.of();
        }

        return inspections.stream()
                .filter(inspection -> projectIds.contains(inspection.getProjectOrderId()))
                .collect(Collectors.toMap(
                        InspectionResponseDto::getProjectOrderId,
                        inspection -> inspection,
                        (first, second) -> first
                ));
    }

    public InspectionResponseDto getInspectionById(UUID id) {
        return inspectionClient.getInspection(id).getBody();
    }

    public List<InspectionResponseDto> getInspectionsByProjectOrderId(UUID projectOrderId) {
        return inspectionClient.getInspectionsByProjectOrderId(projectOrderId).getBody();
    }

    public InspectionResponseDto createInspection(InspectionCreateRequestDto request) {
        return inspectionClient.createInspection(request);
    }

    public InspectionResponseDto requestInspectionForOrder(UUID orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if (!order.isInstallationRequired()) {
            throw new InspectionManagementException("Inspection is needed only when installation is required.");
        }

        ensureInspectionDoesNotExist(order.getId());

        InspectionCreateRequestDto requestDto = new InspectionCreateRequestDto();
        requestDto.setProjectId(order.getId());
        requestDto.setProjectType(ProjectType.ORDER);
        requestDto.setCustomerName(order.getUser().getFirstName() + " " + order.getUser().getLastName());
        requestDto.setProjectAddress(order.getAddress());
        requestDto.setHeightMeters(order.getScaffold().getHeight());
        requestDto.setAreaSqMeters(order.getScaffold().getHeight() * order.getScaffold().getLength() * order.getQuantity());
        requestDto.setInspectionDate(LocalDate.now());

        return inspectionClient.createInspection(requestDto);
    }

    public InspectionResponseDto requestInspectionForCustomOrder(UUID customOrderId){
        CustomOrder customOrder = customOrderRepository.findById(customOrderId)
                .orElseThrow(CustomOrderNotFoundException::new);

        if (!customOrder.isInstallationRequired()) {
            throw new InspectionManagementException("Inspection is needed only when installation is required.");
        }

        ensureInspectionDoesNotExist(customOrder.getId());

        InspectionCreateRequestDto requestDto = new InspectionCreateRequestDto();
        requestDto.setProjectId(customOrder.getId());
        requestDto.setProjectType(ProjectType.CUSTOM_ORDER);
        requestDto.setCustomerName(customOrder.getUser().getFirstName() + " " + customOrder.getUser().getLastName());
        requestDto.setProjectAddress(customOrder.getAddress());
        requestDto.setHeightMeters(customOrder.getHeight());
        requestDto.setAreaSqMeters(customOrder.getHeight() * customOrder.getLength());
        requestDto.setInspectionDate(LocalDate.now());

        return inspectionClient.createInspection(requestDto);
    }

    public InspectionResponseDto submitReport(UUID id, InspectionReportRequestDto request) {
        return inspectionClient.submitReport(id, request);
    }

    public void deleteInspection(UUID id) {
        inspectionClient.deleteInspection(id);
    }

    private void ensureInspectionDoesNotExist(UUID projectId) {
        List<InspectionResponseDto> existingInspections = getInspectionsByProjectOrderId(projectId);

        if (existingInspections != null && !existingInspections.isEmpty()) {
            throw new InspectionManagementException("Inspection request already exists for this project.");
        }
    }

}
