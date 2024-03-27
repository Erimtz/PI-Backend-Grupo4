package com.gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountPurchaseDTO {
    private Long accountId;
    private Long userId;
    private BigDecimal creditBalance;
    private String rank;
    private List<PurchaseResponseDTO> purchases;
}