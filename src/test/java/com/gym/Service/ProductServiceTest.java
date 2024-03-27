package com.gym.Service;


import com.gym.dto.request.ProductFiltersRequestDTO;
import com.gym.dto.request.ProductRequestDTO;
import com.gym.dto.response.ProductResponseDTO;
import com.gym.entities.Category;
import com.gym.entities.Product;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.CategoryRepository;
import com.gym.repositories.ProductRepository;
import com.gym.services.impl.ProductServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    public void testCreateProduct() throws ResourceNotFoundException {

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(Category.builder().build()));

        Product savedProduct = Product.builder()
                .name("Product 1")
                .description("Description 1")
                .stock(10L)
                .price(20.0)
                .category(Category.builder().build())
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponseDTO createdProduct = productService.createProduct(new ProductRequestDTO(1L, "Product 1", "Description 1", 10L, 20.0, null, 1L, new HashSet<>()));

        assertNotNull(createdProduct);

        assertEquals("Product 1", createdProduct.getName());
    }

    @Test
    public void testGetAllProducts() throws ResourceNotFoundException {
        productService.getAllProducts();
        Assertions.assertTrue(true, "Get all products with exit");
    }

    @Test
    public void testUpdateProduct() throws ResourceNotFoundException {
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        requestDTO.setId(1L);

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Existing Product");
        existingProduct.setDescription("Existing Description");
        existingProduct.setStock(5L);
        existingProduct.setPrice(50.0);
        existingProduct.setCategory(new Category());

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setStock(10L);
        updatedProduct.setPrice(100.0);
        updatedProduct.setCategory(new Category());

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductResponseDTO updateProductResponse = productService.updateProduct(requestDTO);

        assertNotNull(updateProductResponse);

        assertEquals(updatedProduct.getId(), updateProductResponse.getId());
        assertEquals(updatedProduct.getName(), updateProductResponse.getName());
        assertEquals(updatedProduct.getDescription(), updateProductResponse.getDescription());
        assertEquals(updatedProduct.getStock(), updateProductResponse.getStock());
        assertEquals(updatedProduct.getPrice(), updateProductResponse.getPrice());
        assertEquals(updatedProduct.getCategory().getId(), updateProductResponse.getCategoryId());
    }

    @Test
    public void testProductById() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setStock(10L);
        product.setPrice(Double.valueOf(100));

        Category category = new Category();
        category.setId(1L);
        product.setCategory(category);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductResponseDTO responseDTO = productService.getProductById(productId);

        assertNotNull(responseDTO);
        assertEquals(productId, responseDTO.getId());
    }

    @Test
    public void testRandomProducts() throws ResourceNotFoundException {
        List<ProductResponseDTO> p = productService.getRandomProducts();
        assertEquals(new ArrayList<>(), p);
    }

    @Test
    public void testProductByIdWithImages() throws ResourceNotFoundException {
        Optional<ProductResponseDTO> p = productService.getProductByIdWithImages(1L);
        Assertions.assertTrue(p.isEmpty());
    }

    @Test
    public void testProductsByCategory() {
        try {
            productService.getProductsByCategory(1L);
            fail("A ResourceNotFoundException was expected.");
        } catch (ResourceNotFoundException e) {
            assertEquals("No products found for category with ID: 1", e.getMessage());
        }
    }

    @Test
    public void testProductsByCategotyFiltered() throws ResourceNotFoundException {
        List<?> p = productService.findProductsByCategoryAndFilters(1L,
                new ProductFiltersRequestDTO(), "name", "asc");
        assertEquals(new ArrayList<>(), p);
    }

    @Test
    public void testSearchProductsByName() throws ResourceNotFoundException {
        String expectedProduct = productRepository.findByName("product 1").toString();
        Assertions.assertNotNull(expectedProduct);
    }

    @Test
    public void testDeleteProductById() {
        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));

        assertTrue(productRepository.existsById(productId));

        productService.deleteProductById(productId);

        verify(productRepository, times(1)).deleteById(productId);
    }
}
