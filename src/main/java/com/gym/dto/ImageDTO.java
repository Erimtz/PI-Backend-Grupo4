package com.gym.dto;

import com.gym.entities.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
    private Long id;
    private String title;
    private String url;
    private Long productId; // Id del producto al que pertenece esta imagen

    // Constructor para crear un DTO a partir de una entidad Image
//    public ImageDTO(Image image) {
//        this.id = image.getId();
//        this.title = image.getTitle();
//        this.url = image.getUrl();
//        if (image.getProduct() != null) {
//            this.productId = image.getProduct().getId();
//        }
//    }

//    // Método para convertir un DTO a una entidad Image
//    public Image toEntity() {
//        Image image = new Image();
//        image.setId(this.id);
//        image.setTitle(this.title);
//        image.setUrl(this.url);
//        // Aquí no establecemos el producto, ya que normalmente se establece al crear la imagen en el servicio
//        return image;
//    }
}