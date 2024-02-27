package com.gym.services.ale;

import com.gym.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    List<CategoryDTO> getAllCategories();
    CategoryDTO getCategoryById(Long id);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO updateCategory(CategoryDTO categoryDTO);
    void deleteCategoryById(Long id);
}
