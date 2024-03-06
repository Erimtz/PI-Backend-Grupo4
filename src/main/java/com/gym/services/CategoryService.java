package com.gym.services;

import com.gym.dto.CategoryDTO;
import com.gym.entities.Category;

import java.util.List;

public interface CategoryService {

    List<CategoryDTO> getAllCategories();
    CategoryDTO getCategoryById(Long id);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO updateCategory(CategoryDTO categoryDTO);
    String deleteCategoryById(Long id);
    CategoryDTO convertToDto(Category category);
}
