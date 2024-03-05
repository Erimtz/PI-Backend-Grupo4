package com.gym.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.dto.ImageDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "images")
public class Image implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;
    @Column
    private String url;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    public ImageDTO toDto() {
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setId(id);
        imageDTO.setTitle(title);
        imageDTO.setUrl(url);
        imageDTO.setProduct(product);
        return imageDTO;
    }

    private static final long serialVersionUID = 1L;
}
