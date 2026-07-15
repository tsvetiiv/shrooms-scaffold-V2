package com.shrooms.scaffold.inspection;

import com.shrooms.scaffold.model.dto.inspection.InspectionCreateRequestDto;
import com.shrooms.scaffold.model.dto.inspection.InspectionReportRequestDto;
import com.shrooms.scaffold.model.dto.inspection.InspectionResponseDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "inspection-client",
        url = "${inspection-svc-base-url}",
        configuration = InspectionClientConfig.class)
public interface InspectionClient {

    @GetMapping
    ResponseEntity<List<InspectionResponseDto>> getInspections();

    @GetMapping("/{id}")
    ResponseEntity<InspectionResponseDto> getInspection(@PathVariable("id") UUID id);


    @GetMapping("/by-project/{projectOrderId}")
    ResponseEntity<List<InspectionResponseDto>> getInspectionsByProjectOrderId(@PathVariable("projectOrderId") UUID projectOrderId);

    @PostMapping
    InspectionResponseDto createInspection(@Valid @RequestBody InspectionCreateRequestDto request);

    @PutMapping("/{id}/report")
    InspectionResponseDto submitReport(@PathVariable("id") UUID id,
                                       @Valid @RequestBody InspectionReportRequestDto request);

    @DeleteMapping("/{id}")
    void deleteInspection(@PathVariable("id") UUID id);


}
