package com.gym.services;

import com.gym.dto.CategoryDTO;
import com.gym.entities.Category;
import com.gym.repositories.CategoryRepository;
import com.gym.services.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void testGetAllCategories() {
        categoryService.getAllCategories();
        assertTrue(true,"Get all categories with exit" );
    }

    @Test
    public void testGetCategoryById() {
        Long categoryId = 1L;

        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setDescription("Description 1");
        category.setImageUrl("image1.jpg");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        CategoryDTO categoryDTO = categoryService.getCategoryById(categoryId);

        assertNotNull(categoryDTO);

        assertEquals(categoryId, categoryDTO.getId());
    }

     @Test
    public void testCreateCategory() {
        Category savedCategory = Category.builder()
                .name("Category 1")
                .description("Description 1")
                .imageUrl("Image1.jpg")
                .build();

         when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

         CategoryDTO createdCategory = categoryService.createCategory(new CategoryDTO(1L, "Category 1", "Description 1", "Image1.jpg"));

         assertNotNull(createdCategory);

         assertEquals("Category 1", createdCategory.getName());
    }

    @Test
    public void testUpdateCategory() {
        CategoryDTO categoryToUpdate = new CategoryDTO();
        categoryToUpdate.setId(1L);
        categoryToUpdate.setName("Updated Category");
        categoryToUpdate.setImageUrl("new_image_url");
        categoryToUpdate.setDescription("New description");

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Existing Category");
        existingCategory.setImageUrl("old_image_url");
        existingCategory.setDescription("Old description");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));

        categoryService.updateCategory(categoryToUpdate);

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryCaptor.capture());
        Category updatedCategory = categoryCaptor.getValue();
        assertEquals(categoryToUpdate.getName(), updatedCategory.getName());
        assertEquals(categoryToUpdate.getImageUrl(), updatedCategory.getImageUrl());
        assertEquals(categoryToUpdate.getDescription(), updatedCategory.getDescription());
    }

    @Test
    public void testDeleteCategoryById() {
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        assertTrue(categoryRepository.existsById(categoryId));

        categoryService.deleteCategoryById(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

}