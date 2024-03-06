package com.gym.dto;

import com.gym.entities.Category;
import com.gym.entities.Image;
import com.gym.entities.Product;
import com.gym.entities.Purchase;
import com.gym.services.CategoryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseProductDTO {

    private Long id;
    private String name;
    private String description;
    private Long stock;
    private Double price;
//    private Purchase purchase;
    private Long categoryId;
    private Set<Image> images;

    public Product responseProductDTOToEntity(ResponseProductDTO responseProductDTO, Category category){
        Product product = Product.builder()
                .name(responseProductDTO.getName())
                .description(responseProductDTO.getDescription())
                .stock(responseProductDTO.getStock())
                .price(responseProductDTO.getPrice())
                .category(category)
                .images(responseProductDTO.getImages())
                .build();
        return product;
    }
}
