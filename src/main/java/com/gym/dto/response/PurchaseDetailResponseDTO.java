package com.gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDetailResponseDTO {
    private String productName;
    private int quantity;
    private Double subtotal;
}