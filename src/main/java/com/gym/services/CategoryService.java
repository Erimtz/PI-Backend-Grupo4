package com.gym.services;

import com.gym.dto.CategoryDTO;
import com.gym.entities.Category;

import java.util.List;


public interface CategoryService {
    List<Category> getAllCategories();
    CategoryDTO getCategoryById(Long id);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
    String deleteCategoryById(Long id);
}
