package com.gym.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStockPurchaseDTO {
    private Long productId;
    private Long subtractStock;
}
