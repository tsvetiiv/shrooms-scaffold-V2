package com.shrooms.scaffold.model.dto.inspection;

import com.shrooms.scaffold.model.enums.inspection.AccessDifficulty;
import com.shrooms.scaffold.model.enums.inspection.GroundCondition;
import com.shrooms.scaffold.model.enums.inspection.SafetyRisk;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InspectionReportRequestDto {

    @NotBlank
    private String inspectorName;
    @Positive(message = "Height must be a positive number")
    private double heightMeters;
    @Positive(message = "Area must be a positive number")
    private double areaSqMeters;
    @NotNull
    private AccessDifficulty accessDifficulty;
    @NotNull
    private GroundCondition groundCondition;
    @NotNull
    private SafetyRisk safetyRisk;
    @Size(max = 1000)
    private String notes;
}
