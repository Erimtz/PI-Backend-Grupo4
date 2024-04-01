package com.gym.controllers;

import com.gym.dto.request.ProductFiltersRequestDTO;
import com.gym.dto.request.ProductRequestDTO;
import com.gym.dto.response.ProductResponseDTO;
import com.gym.entities.Product;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class ProductController {

    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    private final ProductService productService;

    @Operation(summary = "Get all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))
            })
    })
    @GetMapping("/get-all")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(){
        var auth =  SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.getPrincipal());
        System.out.println(auth.getAuthorities());
        System.out.println(auth.isAuthenticated());

        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(summary = "Get 8 random products")
    @GetMapping("/random")
    public List<ProductResponseDTO> getRandomProducts() {
        return productService.getRandomProducts();
    }

    @Operation(summary = "Get product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))
            }), @ApiResponse(responseCode = "404", description = "Product not found",content = @Content),
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getProductById (@PathVariable Long id) throws ResourceNotFoundException {
        ProductResponseDTO productResponseDTO = productService.getProductById(id);
        if (productResponseDTO != null){
            return new ResponseEntity<>(productResponseDTO, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product associated with the provided ID was found.");
    }

    @Operation(summary = "Get product by ID with images")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "404", description = "Product not found",content = @Content)
    })
    @GetMapping("/get-with-images/{id}")
    public ResponseEntity<?> getProductByIdWithImages (@PathVariable Long id) throws ResourceNotFoundException {
        Optional<ProductResponseDTO> productResponseOptional  = productService.getProductByIdWithImages(id);
        if (productResponseOptional.isPresent()) {
            return new ResponseEntity<>(productResponseOptional.get(), HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product associated with the provided ID was found.");
        }
    }

    @Operation(summary = "Get all products by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "404", description = "Products not found",content = @Content),
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable(name = "categoryId") Long categoryId) {
        try {
            List<ProductResponseDTO> products = productService.getProductsByCategory(categoryId);
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get products by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "404", description = "Products not found",content = @Content),
            @ApiResponse(responseCode = "400", description = "Parameter error",content = @Content)
    })
    @GetMapping("/filter/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategoryFiltered(
            @PathVariable(name = "categoryId") Long categoryId,
            @RequestBody ProductFiltersRequestDTO request,
            @RequestParam(name = "orderBy", required = false, defaultValue = "id") String orderBy,
            @RequestParam(name = "orderDirection", required = false, defaultValue = "asc") String orderDirection) {
        try {
            List<ProductResponseDTO> filteredProducts = productService.findProductsByCategoryAndFilters(categoryId, request, orderBy, orderDirection);
            if (filteredProducts.isEmpty()) {
                return new ResponseEntity<>("No products found matching the search criteria.", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(filteredProducts, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Search products by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "404", description = "Products not found",content = @Content),
            @ApiResponse(responseCode = "400", description = "Parameter error",content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchProductsByName(@RequestParam(name = "product") String product) {
        try {
            List<ProductResponseDTO> foundProducts = productService.searchProductsByName(product);
            if (foundProducts.isEmpty()) {
                return new ResponseEntity<>("No products found matching the search criteria.", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(foundProducts, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Search products by filtered name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "404", description = "Products not found",content = @Content),
            @ApiResponse(responseCode = "400", description = "Parameter error",content = @Content)
    })
    @GetMapping("/filter/search")
    public ResponseEntity<?> searchProductsByNameFiltered(
            @RequestParam(name = "product") String product,
            @RequestBody ProductFiltersRequestDTO request,
            @RequestParam(name = "orderBy", defaultValue = "name") String orderBy,
            @RequestParam(name = "orderDirection", defaultValue = "asc") String orderDirection) {
        try {
            List<ProductResponseDTO> filteredProducts = productService.searchProductsByNameAndFilters(product, request, orderBy, orderDirection);
            if (filteredProducts.isEmpty()) {
                return new ResponseEntity<>("No products found matching the search criteria.", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(filteredProducts, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Add a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product added successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "500", description = "Response error",content = @Content),
            @ApiResponse(responseCode = "400", description = "Parameter error",content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequestDTO productRequest) {
        try {
            ProductResponseDTO productResponse = productService.createProduct(productRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
        } catch (ValidationException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create product: " + e.getMessage());
        }
    }

    @Operation(summary = "Update a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "500", description = "Response error",content =
            @Content),
            @ApiResponse(responseCode = "400", description = "Parameter error",content =
            @Content),
            @ApiResponse(responseCode = "404", description = "Product not found",content =
            @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody ProductRequestDTO productRequestDTO){
        try {
            if (productId == null) {
                return ResponseEntity.badRequest().body("Product ID cannot be null.");
            }
            if (!productId.equals(productRequestDTO.getId())) {
                return ResponseEntity.badRequest().body("Product ID in path variable does not match ID in request body.");
            }
            ProductResponseDTO updatedProduct = productService.updateProduct(productRequestDTO);
            return ResponseEntity.ok(updatedProduct);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update product.");
        }
    }

    @Operation(summary = "Delete a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "404", description = "Product not found",content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long id) {
        try {
            productService.deleteProductById(id);
            return new ResponseEntity<>("Product disposed correctly.", HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
    }
}
