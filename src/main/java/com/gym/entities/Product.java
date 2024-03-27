package com.gym.entities;

import com.gym.dto.ProductDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
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
//    @Schema(description = "ID único del producto", example = "1")
    private Long id;

    @Column(name = "name")
//    @Schema(description = "Nombre del producto", example = "Camiseta deportiva")
    private String name;

    @Column(name = "description")
//    @Schema(description = "Descripción del producto", example = "Camiseta deportiva de alta calidad")
    private String description;

    @Column(name = "stock")
//    @Schema(description = "Cantidad en stock del producto", example = "100")
    private Long stock;

    @Column(name = "price")
//    @Schema(description = "Precio del producto", example = "29.99")
    private Double price;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "category_id",nullable = false)
//    @Schema(description = "Categoría a la que pertenece el producto")
    private Category category;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    private Set<Image> images = new HashSet<>();

    @OneToMany(mappedBy = "product")
    private Set<PurchaseDetail> purchaseDetails;

    public Product(Long productId) {
    }

    public ProductDTO toDto() {
        ProductDTO productDto = new ProductDTO();
        productDto.setId(id);
        productDto.setName(name);
        productDto.setDescription(description);
        productDto.setStock(stock);
        productDto.setPrice(price);
        productDto.setCategory(category);
        productDto.setImages(images);

        return productDto;
    }

    public void addImage(Image image) {
        this.images.add(image);
        image.setProduct(this);
    }

    public void removeImage(Image image) {
        this.images.remove(image);
        image.setProduct(null);
    }
}
