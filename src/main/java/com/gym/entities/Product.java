package com.gym.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

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

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToOne(targetEntity = Purchase.class)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product")
    private Set<Image> images;

    public Product(Long productId) {
    }

    public ProductDTO toDto() {
        ProductDTO productDto = new ProductDTO();
        productDto.setId(id);
        productDto.setName(name);
        productDto.setDescription(description);
        productDto.setStock(stock);
        productDto.setPurchase(purchase);
        productDto.setCategory(category);
        productDto.setImages(images);

        return productDto;
    }

}
