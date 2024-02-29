package com.gym.dto;

import com.gym.entities.Category;
import com.gym.entities.Image;
import com.gym.entities.Purchase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestProductDTO {

    private Long id;
    private String name;
    private String description;
    private Long stock;
    private Double price;
    private Purchase purchase;
    private Long categoryId;
    private Set<Image> images;
}
