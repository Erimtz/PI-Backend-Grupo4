package com.gym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponResponseDTO {

    private Long id;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private Double amount;
    private Boolean spent;
    private Long accountId;
}