package com.gym.services;

import com.gym.dto.ProductDTO;
import java.util.*;



public interface ProductService {

    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(Long id);
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(ProductDTO productDTO);
    void deleteProductById(Long id);

    ProductDTO getProductsByCategory(Long categoryId);

    List<ProductDTO> getProductsByName(String name);

    List<ProductDTO> getProductsByPriceRange(Double minPrice, Double maxPrice);

    List<ProductDTO> getAllProductSortedByPriceAsc();

    List<ProductDTO> getAllProductSortedByPriceDesc();
}
