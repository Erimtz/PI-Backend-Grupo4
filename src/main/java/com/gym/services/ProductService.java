package com.gym.services;

import com.gym.dto.request.ProductFiltersRequestDTO;
import com.gym.dto.request.ProductRequestDTO;
import com.gym.dto.response.ProductResponseDTO;
import com.gym.entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO getProductById(Long id);
    Optional<ProductResponseDTO> getProductByIdWithImages(Long id);
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    ProductResponseDTO updateProduct(ProductRequestDTO productRequestDTO);
    Product updateStockPurchase(Long productId, Long subtractStock);
    void deleteProductById(Long id);

    List<ProductResponseDTO> getProductsByCategory(Long categoryId);
    List<ProductResponseDTO> findProductsByCategoryAndFilters(Long categoryId, ProductFiltersRequestDTO request, String orderBy, String orderDirection);

    List<ProductResponseDTO> searchProductsByName(String searchTerm);

    List<ProductResponseDTO> searchProductsByNameAndFilters(String searchTerm, ProductFiltersRequestDTO request, String orderBy, String orderDirection);

    ProductResponseDTO convertToDto(Product product);

    List<ProductResponseDTO> getRandomProducts();
}
