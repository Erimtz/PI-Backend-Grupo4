package com.gym.controllers.ale;

import com.gym.dto.RequestProductDTO;
import com.gym.dto.ResponseProductDTO;
import com.gym.repositories.ImageRepository;
import com.gym.repositories.ProductRepository;
import com.gym.services.ale.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
//    private CategoryService categoryService;
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Agregar un producto")
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@Valid @RequestBody RequestProductDTO productRequest) {
        try {
            ResponseProductDTO productResponse = productService.createProduct(productRequest);
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
}
