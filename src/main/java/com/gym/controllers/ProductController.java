package com.gym.controllers;

import com.gym.dto.request.ProductFiltersRequestDTO;
import com.gym.dto.request.ProductRequestDTO;
import com.gym.dto.response.ProductResponseDTO;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.ImageRepository;
import com.gym.repositories.ProductRepository;
import com.gym.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/product")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class ProductController {

    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    @Autowired
    private ProductRepository productoRepository;
    @Autowired
    private ImageRepository imagenRepository;
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Traer todos los productos")
    @GetMapping("/get-all")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(){
        var auth =  SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.getPrincipal());
        System.out.println(auth.getAuthorities());
        System.out.println(auth.isAuthenticated());

        return ResponseEntity.ok(productService.getAllProducts());
    }
    @Operation(summary = "Traer 8 productos aleatorios")
    @GetMapping("/random")
    public List<ProductResponseDTO> getRandomProducts() {
        return productService.getRandomProducts();
    }

    @Operation(summary = "Traer el producto por ID")
    @GetMapping("/get/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById (@PathVariable Long id) throws ResourceNotFoundException {
        ProductResponseDTO productResponseDTO = productService.getProductById(id);
        if (productResponseDTO !=null){
            return new ResponseEntity<>(productResponseDTO, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("The product with id " + id + " has not been found.");
    }

    @Operation(summary = "Traer el producto por ID con imagenes")
    @GetMapping("/get-with-images/{id}")
    public ResponseEntity<ProductResponseDTO> getProductByIdWithImages (@PathVariable Long id) throws ResourceNotFoundException {
        Optional<ProductResponseDTO> productResponseOptional  = productService.getProductByIdWithImages(id);
        if (productResponseOptional.isPresent()) {
            return new ResponseEntity<>(productResponseOptional.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("The product with id " + id + " has not been found.");
        }
    }

    @Operation(summary = "Traer todos los productos por categoria")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable(name = "categoryId") Long categoryId) {
        try {
            List<ProductResponseDTO> products = productService.getProductsByCategory(categoryId);
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/filter/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategotyFiltered(
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

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Agregar un producto")
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

    @Operation(summary = "Actualizar un producto")
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

    @Operation(summary = "Eliminar un producto por ID")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long id) throws ResourceNotFoundException {
        productService.deleteProductById(id);
        return new ResponseEntity<>("Product disposed correctly.", HttpStatus.OK);
    }
}
