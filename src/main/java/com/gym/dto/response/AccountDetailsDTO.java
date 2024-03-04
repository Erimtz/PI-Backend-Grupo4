package com.gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDetailsDTO {

    private Long id;
    private Long userId;
    private String document;
    private BigDecimal creditBalance;
    private String rank;
}
