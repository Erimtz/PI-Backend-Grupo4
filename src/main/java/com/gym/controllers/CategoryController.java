package com.gym.controllers;

import com.gym.dto.CategoryDTO;
import com.gym.entities.Category;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.security.entities.UserEntity;
import com.gym.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/category")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Traer todas las categorias")
    @GetMapping("/get-all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorias obtenidas con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Category.class))}),
            @ApiResponse(responseCode = "204", description = "No content",content = @Content),
            @ApiResponse(responseCode = "500", description = "Error de respuesta",content = @Content)})
    public ResponseEntity<?> getAllCategories() {
        try {
            List<CategoryDTO> categoriesDTO = categoryService.getAllCategories();
            if (categoriesDTO.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body("There is no categories yet");
            }
            return ResponseEntity.ok(categoriesDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve categories: " + e.getMessage());
        }
    }

    @Operation(summary = "Traer la categoria por ID")
    @GetMapping("/get/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria obtenida con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Category.class))
            }),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada",content =
            @Content),
            @ApiResponse(responseCode = "500", description = "Error de respuesta",content =
            @Content)
    })
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
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

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Agregar una categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria creada con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Category.class))
            }),
            @ApiResponse(responseCode = "500", description = "Error de respuesta",content =
            @Content),
            @ApiResponse(responseCode = "400", description = "Error de parametro",content =
            @Content),
    })
    @PostMapping("/create")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryRequest) {
        try {
            CategoryDTO categoryResponse = categoryService.createCategory(categoryRequest);
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

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria actualizada con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Category.class))
            }),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada",content =
            @Content),
            @ApiResponse(responseCode = "500", description = "Error de respuesta",content =
            @Content)
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryRequest) {
        categoryRequest.setId(id);
        try {
            CategoryDTO categoryResponse = categoryService.updateCategory(categoryRequest);
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

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar una categoria por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria eliminada con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Category.class))
            }),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada",content =
            @Content),
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategoryById(@PathVariable Long id) {
        try {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Category with ID " + id + " has successfully deleted");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category with ID " + id + " not found");
        }
    }
}
