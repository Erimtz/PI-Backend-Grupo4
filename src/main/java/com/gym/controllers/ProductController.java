package com.gym.controllers;

import com.gym.dto.request.ProductFiltersRequestDTO;
import com.gym.dto.request.ProductRequestDTO;
import com.gym.dto.response.ProductResponseDTO;
import com.gym.entities.Product;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.ImageRepository;
import com.gym.repositories.ProductRepository;
import com.gym.security.entities.UserEntity;
import com.gym.services.ProductService;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos obtenidos con exito", content = {
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

    @Operation(summary = "Traer 8 productos aleatorios")
    @GetMapping("/random")
    public List<ProductResponseDTO> getRandomProducts() {
        return productService.getRandomProducts();
    }

    @Operation(summary = "Traer el producto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto obtenidos con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))
            }), @ApiResponse(responseCode = "404", description = "Suscripción no encontradas",content = @Content),
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getProductById (@PathVariable Long id) throws ResourceNotFoundException {
        ProductResponseDTO productResponseDTO = productService.getProductById(id);
        if (productResponseDTO !=null){
            return new ResponseEntity<>(productResponseDTO, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la suscripción asociada a la cuenta proporcionada.");
    }

    @Operation(summary = "Traer el producto por ID con imagenes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto obtenido con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "404", description = "Suscripción no encontradas",content = @Content)
    })
    @GetMapping("/get-with-images/{id}")
    public ResponseEntity<?> getProductByIdWithImages (@PathVariable Long id) throws ResourceNotFoundException {
        Optional<ProductResponseDTO> productResponseOptional  = productService.getProductByIdWithImages(id);
        if (productResponseOptional.isPresent()) {
            return new ResponseEntity<>(productResponseOptional.get(), HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la suscripción asociada a la cuenta proporcionada.");
        }
    }

    @Operation(summary = "Traer todos los productos por categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos obtenidos con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "404", description = "Productos no encotrados",content = @Content),
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

    @Operation(summary = "Obtener productos por categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos obtenidos con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "404", description = "Productos no encontrados",content = @Content),
            @ApiResponse(responseCode = "400", description = "Error de parametros",content = @Content)
    })
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

    @Operation(summary = "Buscar productos por nombre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos obtenidos con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "404", description = "Productos no encontrados",content = @Content),
            @ApiResponse(responseCode = "400", description = "Error de parametros",content = @Content)
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

    @Operation(summary = "Buscar productos por nombre filtrado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos obtenidos con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "404", description = "Productos no encontrados",content = @Content),
            @ApiResponse(responseCode = "400", description = "Error de parametros",content = @Content)
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

    @Operation(summary = "Agregar un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto agregado con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "500", description = "Error de respuesta",content = @Content),
            @ApiResponse(responseCode = "400", description = "Error de parametros",content = @Content)
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

    @Operation(summary = "Actualizar un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "500", description = "Error de respuesta",content =
            @Content),
            @ApiResponse(responseCode = "400", description = "Error de parametros",content =
            @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",content =
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

    @Operation(summary = "Eliminar un producto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado con exito", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long id) {
        try {
            productService.deleteProductById(id);
            return new ResponseEntity<>("Product disposed correctly.", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("No products found", HttpStatus.NOT_FOUND);
        }
    }
}
