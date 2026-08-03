package com.shrooms.scaffold.model.entity.enums;

import com.shrooms.scaffold.model.enums.inspection.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InspectionEnumsTest {

    @Test
    public void projectType_shouldContainExpectedValues() {
        Assertions.assertEquals(2, ProjectType.values().length);

        assertEquals(ProjectType.ORDER, ProjectType.valueOf("ORDER"));
        assertEquals(ProjectType.CUSTOM_ORDER, ProjectType.valueOf("CUSTOM_ORDER"));
    }

    @Test
    public void inspectionStatus_shouldContainExpectedValues() {
        Assertions.assertEquals(4, InspectionStatus.values().length);

        assertEquals(InspectionStatus.REQUESTED, InspectionStatus.valueOf("REQUESTED"));
        assertEquals(InspectionStatus.REPORT_SUBMITTED, InspectionStatus.valueOf("REPORT_SUBMITTED"));
        assertEquals(InspectionStatus.COMPLETED, InspectionStatus.valueOf("COMPLETED"));
        assertEquals(InspectionStatus.CANCELLED, InspectionStatus.valueOf("CANCELLED"));
    }

    @Test
    public void recommendedAction_shouldContainExpectedValues() {
        Assertions.assertEquals(3, RecommendedAction.values().length);

        assertEquals(RecommendedAction.APPROVE, RecommendedAction.valueOf("APPROVE"));
        assertEquals(RecommendedAction.NEEDS_REVIEW, RecommendedAction.valueOf("NEEDS_REVIEW"));
        assertEquals(RecommendedAction.REJECT, RecommendedAction.valueOf("REJECT"));
    }

    @Test
    public void accessDifficulty_shouldContainExpectedValues() {
        Assertions.assertEquals(3, AccessDifficulty.values().length);

        assertEquals(AccessDifficulty.EASY, AccessDifficulty.valueOf("EASY"));
        assertEquals(AccessDifficulty.MODERATE, AccessDifficulty.valueOf("MODERATE"));
        assertEquals(AccessDifficulty.HARD, AccessDifficulty.valueOf("HARD"));
    }

    @Test
    public void safetyRisk_shouldContainExpectedValues() {
        Assertions.assertEquals(3, SafetyRisk.values().length);

        assertEquals(SafetyRisk.LOW, SafetyRisk.valueOf("LOW"));
        assertEquals(SafetyRisk.MEDIUM, SafetyRisk.valueOf("MEDIUM"));
        assertEquals(SafetyRisk.HIGH, SafetyRisk.valueOf("HIGH"));
    }

    @Test
    public void groundCondition_shouldContainExpectedValues() {
        assertEquals(3, GroundCondition.values().length);

        assertEquals(GroundCondition.STABLE, GroundCondition.valueOf("STABLE"));
        assertEquals(GroundCondition.UNEVEN, GroundCondition.valueOf("UNEVEN"));
        assertEquals(GroundCondition.UNSTABLE, GroundCondition.valueOf("UNSTABLE"));
    }

    @Test
    public void riskLevel_shouldContainExpectedValues() {
        assertEquals(3, RiskLevel.values().length);

        assertEquals(RiskLevel.LOW, RiskLevel.valueOf("LOW"));
        assertEquals(RiskLevel.MEDIUM, RiskLevel.valueOf("MEDIUM"));
        assertEquals(RiskLevel.HIGH, RiskLevel.valueOf("HIGH"));
    }
}