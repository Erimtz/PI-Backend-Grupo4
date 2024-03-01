package com.gym.entities;

import com.gym.dto.CategoryDTO;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "imageUrl")
    private String imageUrl;

    public CategoryDTO toDto() {
        CategoryDTO categoryDto = new CategoryDTO();
        categoryDto.setId(id);
        categoryDto.setTitle(title);
        categoryDto.setDescription(description);
        categoryDto.setImageUrl(imageUrl);
        return categoryDto;
    }
}
