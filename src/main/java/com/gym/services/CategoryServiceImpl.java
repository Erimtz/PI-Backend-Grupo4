package com.gym.services;

import com.gym.dto.CategoryDTO;
import com.gym.entities.Category;
import com.gym.exceptions.DatabaseOperationException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return (List<Category>) categoryRepository.findAll();
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + id + " not found")).toDto();
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        try {
            Category category = Category.builder()
                    .title(categoryDTO.getTitle())
                    .imageUrl(categoryDTO.getImageUrl())
                    .description(categoryDTO.getDescription())
                    .build();
            categoryRepository.save(category);
            return categoryDTO;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while saving category", e);
        }
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category categoryToUpdate = categoryRepository.findById(categoryDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("The product with id " + categoryDTO + " has not been found to be updated."));

        if (categoryDTO.getTitle() != null) {
            categoryToUpdate.setTitle(categoryDTO.getTitle());
        }
        if (categoryDTO.getDescription() != null) {
            categoryToUpdate.setDescription(categoryDTO.getDescription());
        }
        if (categoryDTO.getImageUrl() != null) {
            categoryToUpdate.setImageUrl(categoryDTO.getImageUrl());
        }
        return categoryRepository.save(categoryToUpdate).toDto();
    }

    @Override
    public String deleteCategoryById(Long id) throws ResourceNotFoundException {
        categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The category with id " + id + " has not been found to be deleted."));

        categoryRepository.deleteById(id);
        return "Category with id " + id + " deleted succesfully.";
    }
}
