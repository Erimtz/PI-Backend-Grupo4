package com.gym.dto.response;

import com.gym.entities.Category;
import com.gym.entities.Image;
import com.gym.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Long stock;
    private Double price;
//    private Purchase purchase;
    private Long categoryId;
    private Set<ImageResponseDTO> images;

    public Product responseProductDTOToEntity(ProductResponseDTO productResponseDTO, Category category){
        Set<Image> imageEntities = this.images.stream()
                .map(imageDto -> {
                    Image image = new Image();
                    image.setId(imageDto.getId());
                    image.setTitle(imageDto.getTitle());
                    image.setUrl(imageDto.getUrl());
                    image.setProduct(image.getProduct());
                    return image;
                })
                .collect(Collectors.toSet());

        Product product = Product.builder()
                .name(productResponseDTO.getName())
                .description(productResponseDTO.getDescription())
                .stock(productResponseDTO.getStock())
                .price(productResponseDTO.getPrice())
                .category(category)
                .images(imageEntities)
                .build();
        return product;
    }
}
