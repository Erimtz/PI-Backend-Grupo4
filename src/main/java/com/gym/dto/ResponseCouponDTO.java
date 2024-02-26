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
public class ResponseCouponDTO {

    private Long id;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private Double amount;
    private Boolean spent;
    private Long accountId;
}