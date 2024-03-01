package com.gym.dto;

import com.gym.entities.Category;
import com.gym.entities.Product;
import com.gym.entities.Purchase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long id;

    private String name;

    private String description;

    private Long stock;

    private Double price;

    private Purchase purchase;

    private Category category;

    private String images;

    public Product toEntity() {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setStock(stock);
        product.setPrice(price);
        product.setPurchase(purchase);
        product.setCategory(category);
        product.setImages(images);

        return product;
    }


}

