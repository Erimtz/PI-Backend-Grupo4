package com.gym.dto;

import com.gym.entities.Image;
import com.gym.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
    private Long id;
    private String title;
    private String foto;
    private Product product;

    public Image toEntity() {
        Image image = new Image();
        image.setId(id);
        image.setTitle(title);
        image.setFoto(foto);
        image.setProduct(product);

        return image;
    }
}
