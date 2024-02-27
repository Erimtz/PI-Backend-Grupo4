package com.gym.services.ale;

import com.gym.dto.CategoryDTO;
import com.gym.entities.Category;
import com.gym.entities.Image;
import com.gym.exceptions.DatabaseOperationException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + id + " not found"));
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
            return convertToDto(category);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while saving category", e);
        }
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryDTO.getId());
        if (categoryOptional.isPresent()) {
            Category category =categoryOptional.get();

            if (categoryDTO.getTitle() != null) {
                category.setTitle(categoryDTO.getTitle());
            }

            if (categoryDTO.getImageUrl() != null) {
                category.setImageUrl(categoryDTO.getImageUrl());
            }

            if (categoryDTO.getDescription() != null) {
                category.setDescription(categoryDTO.getDescription());
            }
            categoryRepository.save(category);
            return convertToDto(category);
        } else {
            throw new ResourceNotFoundException("Category with ID " + categoryDTO.getId() + " not found");
        }
    }

    @Override
    public void deleteCategoryById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category with ID " + id + " not found");
        }
        categoryRepository.deleteById(id);
    }

    private CategoryDTO convertToDto(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getTitle(),
                category.getImageUrl(),
                category.getDescription()
        );
    }
}
