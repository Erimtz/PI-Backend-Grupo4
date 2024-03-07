package com.gym.services;

import com.gym.dto.*;
import com.gym.dto.request.UpdateStockPurchaseDTO;
import com.gym.entities.Product;

import java.util.List;

public interface ProductService {

    List<ResponseProductDTO> getAllProducts();
    ResponseProductDTO getProductById(Long id);
    ResponseProductDTO createProduct(RequestProductDTO requestProductDTO);
    ResponseProductDTO updateProduct(RequestProductDTO requestProductDTO);
    Product updateStockPurchase(Long productId, Long subtractStock);
    void deleteProductById(Long id);

    ProductDTO getProductsByCategory(Long categoryId);

    List<ProductDTO> getProductsByName(String name);

    List<ProductDTO> getProductsByPriceRange(Double minPrice, Double maxPrice);

    List<ProductDTO> getAllProductSortedByPriceAsc();

    List<ProductDTO> getAllProductSortedByPriceDesc();
}
