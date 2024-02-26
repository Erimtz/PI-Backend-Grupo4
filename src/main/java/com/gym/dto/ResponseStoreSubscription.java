package com.gym.dto;

import com.gym.entities.Purchase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseStoreSubscription {

    private Long id;
    private String name;
    private Double price;
    private String description;
    private String imageUrl;
    private String planType;
    private List<Purchase> purchases;
}
