package com.gym.controllers.ale;

import com.gym.dto.CategoryDTO;
import com.gym.services.ale.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categoriesDTO = categoryService.getAllCategories();
        return ResponseEntity.ok(categoriesDTO);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<CategoryDTO> getImageById(@PathVariable Long id) {
        CategoryDTO categoryDTO = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryDTO);
    }

    @PostMapping("/create")
    public ResponseEntity<CategoryDTO> createImage(@Valid @RequestBody CategoryDTO categoryRequest) {
        CategoryDTO categoryResponse = categoryService.createCategory(categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponse);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CategoryDTO> updateImage(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryRequest) {
        categoryRequest.setId(id);
        CategoryDTO categoryResponse = categoryService.updateCategory(categoryRequest);
        return ResponseEntity.ok(categoryResponse);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteImageById(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }
}
