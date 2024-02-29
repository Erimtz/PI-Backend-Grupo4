package com.gym.dto;

import com.gym.entities.Product;
import com.gym.entities.Purchase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDetailDTO {

    private Long id;
    private Integer quantity;
    private Product product;
    private Purchase purchase;
}
