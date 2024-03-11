package com.gym.dto.request;

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
public class CouponUpdateDTO {

    private Long id;
    private LocalDate issueDate;
    @Future
    private LocalDate dueDate;
    @Digits(integer = 4, fraction = 2)
    @Min(0)
    private Double amount;
    private Boolean spent;
    @NotNull
    private Account account;
}
