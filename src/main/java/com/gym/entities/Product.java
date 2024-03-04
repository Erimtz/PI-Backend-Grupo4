package com.gym.entities;

import com.gym.dto.ImageDTO;
import com.gym.dto.ProductDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "stock")
    private Long stock;

    @Column(name = "price")
    private Double price;

    @ManyToOne(targetEntity = Purchase.class)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product")
    @Column(name = "images")
    private Set<Image> images;

    public Product(Long productId) {
    }

    public ProductDTO toDto() {
        ProductDTO productDto = new ProductDTO();
        productDto.setId(id);
        productDto.setName(name);
        productDto.setDescription(description);
        productDto.setStock(stock);
        productDto.setPrice(price);
        productDto.setPurchase(purchase);
        productDto.setCategory(category);
        Set<ImageDTO> imageSet = images.stream()
                .map(Image::toDto)
                .collect(Collectors.toSet());
        productDto.setImages(imageSet);

        return productDto;
    }
}
