package com.gym.dto;

import com.gym.entities.Account;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCouponDTO {


    private LocalDate issueDate;
    @Future
    private LocalDate dueDate;
    @Digits(integer = 4, fraction = 2)
    @Min(0)
    private Double amount;
    @AssertFalse
    private Boolean spent;
    @NotNull
    private Account account;
}