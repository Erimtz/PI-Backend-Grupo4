package com.gym.dto.request;

import com.gym.entities.Image;
import com.gym.entities.Purchase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

    private Long id;
    private String name;
    private String description;
    private Long stock;
    private Double price;
    private Purchase purchase;
    private Long categoryId;
    private Set<Image> images;
}
