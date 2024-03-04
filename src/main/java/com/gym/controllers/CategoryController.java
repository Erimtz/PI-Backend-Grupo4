package com.gym.controllers;

import com.gym.dto.CategoryDTO;
import com.gym.entities.Category;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class CategoryController {
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Traer todas las categorias")
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            List<Category> categoriesDTO = categoryService.getAllCategories();
            if (categoriesDTO.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body("There is no categories yet");
            }
            return ResponseEntity.ok(categoriesDTO.toString());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve categories: " + e.getMessage());
        }
    }

    @Operation(summary = "Traer la categoria por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) throws ResourceNotFoundException {
        try {
            CategoryDTO categoryDTO = categoryService.getCategoryById(id);
            return ResponseEntity.ok(categoryDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Category with ID " + id + " not found");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve category with ID " + id + ": " + e.getMessage());
        }
    }

    @Operation(summary = "Agregar una categoria")
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryDTO categoryDTO) {
        try {
            CategoryDTO categoryResponse = categoryService.createCategory(categoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponse);
        } catch (ValidationException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create category: " + e.getMessage());
        }
    }

    @Operation(summary = "Actualizar una categoria")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) throws ResourceNotFoundException {
        categoryDTO.setId(id);
        try {
            CategoryDTO categoryResponse = categoryService.updateCategory(id, categoryDTO);
            return ResponseEntity.ok(categoryResponse);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Category with ID " + id + " not found");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update category with ID " + id + ": " + e.getMessage());
        }
    }

    @Operation(summary = "Eliminar una categoria por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategoryById(@PathVariable Long id) throws ResourceNotFoundException {
        try {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Category with ID " + id + " has successfully deleted");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category with ID " + id + " not found");
        }
    }
}