package com.gym.dto;

import com.gym.entities.Category;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {

    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private String imageUrl;

    public Category categoryDTOToEntity(CategoryDTO categoryDTO){
        Category category = Category.builder()
                .id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .imageUrl(categoryDTO.getImageUrl())
                .build();
        return category;
    }
}
