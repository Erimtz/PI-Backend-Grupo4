package com.gym.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreSubscriptionCreateDTO {

    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotNull(message = "Price cannot be null")
    private Double price;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @NotBlank(message = "Image URL cannot be blank")
    private String imageUrl;
    @NotBlank(message = "Plan type cannot be blank")
    private String planType;
    @NotBlank(message = "Duration days cannot be blank")
    private Integer durationDays;
//    private List<Purchase> purchases;
}
