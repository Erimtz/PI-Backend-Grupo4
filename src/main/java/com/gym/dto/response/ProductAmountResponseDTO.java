package com.gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAmountResponseDTO {

    private Long id;
    private String name;
    private Double totalSales;
    private Double price;
    private Long categoryId;

}
