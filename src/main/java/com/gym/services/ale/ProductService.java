package com.gym.services.ale;

import com.gym.dto.RequestImageDTO;
import com.gym.dto.RequestProductDTO;
import com.gym.dto.ResponseImageDTO;
import com.gym.dto.ResponseProductDTO;
import com.gym.entities.Product;

import java.util.List;

public interface ProductService {

    List<ResponseProductDTO> getAllProducts();
    ResponseProductDTO getProductById(Long id);
    ResponseProductDTO createProduct(RequestProductDTO requestProductDTO);
    ResponseProductDTO updateProduct(RequestProductDTO requestProductDTO);
    Product updateStockPurchase(Long stock);
    void deleteProductById(Long id);
}
