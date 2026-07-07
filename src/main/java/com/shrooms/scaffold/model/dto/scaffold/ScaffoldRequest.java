package com.shrooms.scaffold.model.dto.scaffold;

import com.shrooms.scaffold.model.entity.scaffold.MaterialType;
import com.shrooms.scaffold.model.entity.scaffold.ScaffoldCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScaffoldRequest {

    @NotBlank(message = "Scaffold name is required")
    @Size(min = 3, max = 50, message = "Scaffold name must be between 3 and 50 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 1000, message = "Description must be between 20 and 1000 characters")
    private String description;

    @Positive(message = "Height must be greater than 0")
    private double height;

    @Positive(message = "Width must be greater than 0")
    private double width;

    @Positive(message = "Length must be greater than 0")
    private double length;

    @NotNull(message = "Material type is required")
    private MaterialType materialType;

    @NotNull(message = "Scaffold category is required")
    private ScaffoldCategory scaffoldCategory;

    @NotNull(message = "Price for rent is required")
    @Positive(message = "Price for rent must be greater than 0")
    private BigDecimal priceForRent;

    @NotNull (message = "Price for sale is required")
    @Positive(message = "Price for sale must be greater than 0")
    private BigDecimal priceForSale;

    @NotBlank(message = "Image URL is required")
    @Size(max = 500,message = "Image URL must be up to 500 characters")
    private String imageUrl;

    private boolean available;
}
