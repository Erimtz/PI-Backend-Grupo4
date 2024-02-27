//package com.gym.controllers;
//
//import com.gym.entities.Category;
//import com.gym.exceptions.ResourceNotFoundException;
//import com.gym.services.CategoryService;
//import io.swagger.v3.oas.annotations.Operation;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/categories")
//@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
//public class CategoryController {
//    private CategoryService categoryService;
//
//    public CategoryController(CategoryService categoryService) {
//        this.categoryService = categoryService;
//    }
//
//    @Operation(summary = "Traer todas las categorias")
//    @GetMapping
//    public ResponseEntity<List<Category>> getAllCategory() {
//        return ResponseEntity.ok(categoryService.getAllCategory());
//    }
//
//    @Operation(summary = "Traer la categoria por ID")
//    @GetMapping("/{id}")
//    public ResponseEntity<Category> getCategory(@PathVariable Long id) throws ResourceNotFoundException {
//        return ResponseEntity.ok(categoryService.getCategory(id));
//    }
//
//    @Operation(summary = "Agregar una categoria")
//    @PostMapping
//    public ResponseEntity<Category> saveCategoria(@RequestBody Category category) {
//        return ResponseEntity.ok(categoryService.saveCategory(category));
//    }
//
//    @Operation(summary = "Actualizar una categoria")
//    @PutMapping("/update/{id}")
//    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) throws ResourceNotFoundException {
//        return ResponseEntity.ok(categoryService.updateCategory(id, category));
//    }
//
//    @Operation(summary = "Eliminar una categoria por ID")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteCategory(@PathVariable Long id) throws ResourceNotFoundException {
//        return ResponseEntity.ok(categoryService.deleteCategory(id));
//    }
//}