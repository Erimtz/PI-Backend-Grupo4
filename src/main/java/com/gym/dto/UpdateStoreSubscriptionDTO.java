package com.gym.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStoreSubscriptionDTO {

    @NotNull(message = "ID cannot be null")
    private Long id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private Double price;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @NotBlank(message = "Image URL cannot be blank")
    private String imageUrl;
    @NotBlank(message = "Plan type cannot be blank")
    private String planType;
    @NotBlank(message = "Duration days cannot be blank")
    private Integer durationDays;
}
