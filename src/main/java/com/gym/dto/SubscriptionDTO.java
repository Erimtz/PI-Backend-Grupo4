package com.gym.dto;

import com.gym.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionDTO {

    private Long id;
    private String name;
    private Double price;
    private String imageUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private String planType;
    private Boolean automaticRenewal;
    private Account account;
}
