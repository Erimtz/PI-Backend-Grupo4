package com.gym.dto.request;

import com.gym.entities.Product;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageRequestDTO {

    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String url;
    @NotNull
    private Product product;

    public void setProductId(Long productId) {
        if (this.product == null) {
            this.product = new Product();
        }
        this.product.setId(productId);
    }
}
