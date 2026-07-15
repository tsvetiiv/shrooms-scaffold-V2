package com.shrooms.scaffold.model.dto.inspection;

import com.shrooms.scaffold.model.enums.inspection.ProjectType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InspectionCreateRequestDto {

    @NotNull
    private UUID projectId;

    @NotNull(message = "Project type is required")
    private ProjectType projectType;

    @NotBlank(message = "Customer name is required")
    @Size(min = 3, message = "Customer name should be min 3 characters")
    private String customerName;

    @NotBlank(message = "Project address is required")
    @Size(min = 4, max = 255, message = "Address should be between 4 and 255 characters")
    private String projectAddress;

    @Positive(message = "Height must be a positive number")
    private double heightMeters;

    @Positive(message = "Area must be a positive number")
    private double areaSqMeters;

    private LocalDate inspectionDate;

    @Size(max = 1000, message = "Notes should be up to 1000 characters")
    private String notes;
}
