package com.shrooms.scaffold.model.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentOrderRequest {

    private UUID scaffoldId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;

    @NotNull(message = "Rental weeks are required")
    @Positive(message = "Rental weeks must be greater than 0")
    private Integer rentalWeeks;

    @NotBlank(message = "Delivery address is required")
    @Size(min = 5, max = 150, message = "Delivery address must be between 5 and 150 characters")
    private String address;

    @NotBlank(message = "Contact phone is required")
    @Size(min = 10, max = 15, message = "Contact phone must be between 10 and 15 characters")
    private String contactPhone;

    private boolean installationRequired;
}
