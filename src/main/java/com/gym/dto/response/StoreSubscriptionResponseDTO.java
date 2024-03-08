package com.gym.dto.response;

import com.gym.entities.Purchase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreSubscriptionResponseDTO {

    private Long id;
    private String name;
    private Double price;
    private String description;
    private String imageUrl;
    private String planType;
    private Integer durationDays;
    private List<Purchase> purchases;
}
