package com.shrooms.scaffold.model.dto.inspection;



import com.shrooms.scaffold.model.enums.inspection.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InspectionResponseDto {

    private UUID id;
    private UUID projectOrderId;
    private ProjectType projectType;
    private String customerName;
    private String projectAddress;
    private String inspectorName;
    private LocalDate inspectionDate;
    private double heightMeters;
    private double areaSqMeters;
    private AccessDifficulty accessDifficulty;
    private GroundCondition groundCondition;
    private SafetyRisk safetyRisk;
    private RiskLevel riskLevel;
    private RecommendedAction recommendedAction;
    private String notes;
    private InspectionStatus status;
}
