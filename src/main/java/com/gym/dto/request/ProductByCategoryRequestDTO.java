package com.gym.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductByCategoryRequestDTO {

    private Double minPrice;
    private Double maxPrice;
    private Boolean hasStock;
}
