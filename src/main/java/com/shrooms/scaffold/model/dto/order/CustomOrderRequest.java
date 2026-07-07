package com.shrooms.scaffold.model.dto.order;

import com.shrooms.scaffold.model.entity.order.OrderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomOrderRequest {

    @NotNull(message = "Height is required")
    @Positive(message = "Height must be greater than 0")
    private Double height;

    @NotNull(message = "Width is required")
    @Positive(message = "Width must be greater than 0")
    private Double width;

    @NotNull(message = "Length is required")
    @Positive(message = "Length must be greater than 0")
    private Double length;

    @NotBlank(message = "Delivery address is required")
    @Size(min = 5, max = 150, message = "Delivery address must be between 5 and 150 characters")
    private String address;

    private boolean installationRequired;

    private LocalDate startDate;

    private LocalDate endDate;

    @NotBlank(message = "Contact phone is required")
    @Size(min = 10, max = 15, message = "Contact phone must be between 10 and 15 characters")
    private String contactPhone;

    @NotBlank(message = "Project description is required")
    @Size(min = 20, max = 150, message = "Project description must be between 20 and 150 characters")
    private String projectDescription;

    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 80, message = "Project name must be between 3 and 80 characters")
    private String projectName;

    @Size(max = 500, message = "Project image URL must be up to 500 characters")
    private String projectImage;

    @NotNull(message = "Request type is required")
    private OrderType orderType;
}
