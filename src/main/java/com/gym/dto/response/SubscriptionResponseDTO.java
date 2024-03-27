package com.gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponseDTO {

    private Long subscriptionId;
    private Long accountId;
    private String document;
    private String name;
    private Double price;
    private String imageUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isExpired;
    private String planType;
    private Boolean automaticRenewal;
}
