package com.shrooms.scaffold.model.dto.ourWork;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OurWorkProjectRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @NotBlank(message = "Description is required")
    private String description;

    private boolean visible;

}
