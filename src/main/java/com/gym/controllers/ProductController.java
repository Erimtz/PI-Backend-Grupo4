package com.gym.controllers;

import com.gym.dto.ProductDTO;
import com.gym.exceptions.BadRequestException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.services.CategoryService;
import com.gym.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class ProductController {

    private ProductService productService;

    private CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @Operation(summary = "Traer todos los productos")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts(){
        var auth =  SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.getPrincipal());
        System.out.println(auth.getAuthorities());
        System.out.println(auth.isAuthenticated());

        return ResponseEntity.ok(productService.getAllProduct());
    }

    @Operation(summary = "Traer el producto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) throws ResourceNotFoundException {
        ProductDTO productDTO = productService.getProduct(id);
        if (productDTO!=null){
            return new ResponseEntity<>(productDTO, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("The product with id " + id + " has not been found.");
    }

    @Operation(summary = "Traer el producto de categoria")
    @GetMapping("/category={category_id}")
    public ResponseEntity<List<ProductDTO>> getProductByCategory(@PathVariable(name = "category_id") Long category_id) throws ResourceNotFoundException{
        return new ResponseEntity<>(productService.getProductsByCategory(category_id),HttpStatus.OK);
    }

    @Operation(summary = "Filtrar productos por nombre")
    @GetMapping("/filterByName")
    public ResponseEntity<List<ProductDTO>> filterProductsByName(@RequestParam(name = "name") String name) {
        List<ProductDTO> productList = productService.getProductsByName(name);
        return ResponseEntity.ok(productList);
    }

    @Operation(summary = "Filtrar productos por rango de precio")
    @GetMapping("/filterByPriceRange")
    public ResponseEntity<List<ProductDTO>> filterProductsByPriceRange(
            @RequestParam(name = "minPrice") Double minPrice,
            @RequestParam(name = "maxPrice") Double maxPrice) {
        List<ProductDTO> productList = productService.getProductsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(productList);
    }

    @Operation(summary = "Ordenar productos por precio ascendente")
    @GetMapping("/sortByPriceAsc")
    public ResponseEntity<List<ProductDTO>> sortProductsByPriceAsc() {
        List<ProductDTO> productList = productService.getAllProductSortedByPriceAsc();
        return ResponseEntity.ok(productList);
    }

    @Operation(summary = "Ordenar productos por precio descendente")
    @GetMapping("/sortByPriceDesc")
    public ResponseEntity<List<ProductDTO>> sortProductsByPriceDesc() {
        List<ProductDTO> productList = productService.getAllProductSortedByPriceDesc();
        return ResponseEntity.ok(productList);
    }

    @Operation(summary = "Agregar un producto")
    @PostMapping
    public ResponseEntity<ProductDTO> saveProduct(@RequestBody ProductDTO productDto) throws BadRequestException, ResourceNotFoundException {
        if (productDto.getPrice() == null) {
            throw new BadRequestException("El precio del producto es requerido.");
        }

        ProductDTO savedProductDto = productService.saveProduct(productDto);

        return new ResponseEntity<>(savedProductDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un producto")
    @PutMapping
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDto) throws ResourceNotFoundException {
        return new ResponseEntity<>(productService.updateProduct(productDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Eliminar un producto por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) throws ResourceNotFoundException {
        return new ResponseEntity<>(productService.deleteProduct(id), HttpStatus.OK);
    }
}
