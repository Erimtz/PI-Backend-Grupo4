package com.gym.services;

import com.gym.entities.Category;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategory(){
        return (List<Category>) categoryRepository.findAll();
    }

    public Category getCategory(Long id) throws ResourceNotFoundException {
        Category categorySearched = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The category with id " + id + " has not been found."));

        return categorySearched;
    }

    public Category saveCategory(Category category){
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long category_id, Category category) throws ResourceNotFoundException {
        Category categoryToUpdate = categoryRepository.findById(category_id)
                .orElseThrow(() -> new ResourceNotFoundException("The product with id " + category_id + " has not been found to be updated."));

        if (category.getTitle() != null) {
            categoryToUpdate.setTitle(category.getTitle());
        }
        if (category.getDescription() != null) {
            categoryToUpdate.setDescription(category.getDescription());
        }
        if (category.getImageUrl() != null) {
            categoryToUpdate.setImageUrl(category.getImageUrl());
        }

        return categoryRepository.save(categoryToUpdate);
    }

    public String deleteCategory(Long id) throws ResourceNotFoundException {
        Category categoryToDelete = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The category with id " + id + " has not been found to be deleted."));

        categoryRepository.deleteById(id);
        return "Category with id " + id + " deleted succesfully.";
    }
}
