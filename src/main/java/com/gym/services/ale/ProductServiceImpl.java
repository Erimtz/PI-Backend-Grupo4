package com.gym.services.ale;

import com.gym.dto.RequestProductDTO;
import com.gym.dto.ResponseProductDTO;
import com.gym.dto.request.UpdateStockPurchaseDTO;
import com.gym.entities.Category;
import com.gym.entities.Image;
import com.gym.entities.Product;
import com.gym.exceptions.NotEnoughStockException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.CategoryRepository;
import com.gym.repositories.ImageRepository;
import com.gym.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<ResponseProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
    }

    @Override
    public ResponseProductDTO createProduct(RequestProductDTO requestProductDTO) {
        Optional<Category> categoryOptional = categoryRepository.findById(requestProductDTO.getCategoryId());
        if (categoryOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product with ID " + requestProductDTO.getCategoryId() + " not found");
        }
        Category category = categoryOptional.get();

        Set<Image> images = new HashSet<>();

        Product product = new Product();
        product.setName(requestProductDTO.getName());
        product.setPrice(requestProductDTO.getPrice());
        product.setDescription(requestProductDTO.getDescription());
        product.setStock(requestProductDTO.getStock());
        product.setCategory(category);
        product.setImages(images);

        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    @Override
    public ResponseProductDTO updateProduct(RequestProductDTO requestProductDTO) {
        return null;
    }

    @Override
    public Product updateStockPurchase(Long productId, Long subtractStock) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (product.getStock() >= subtractStock) {
                product.setStock(product.getStock() - subtractStock);
                return productRepository.save(product);
            } else {
                throw new NotEnoughStockException("The stock is not enough to buy " + subtractStock + " unit(s). Only left " + product.getStock() + " unit(s)");
            }
        } else {
            throw new ResourceNotFoundException("Product with ID " + productId + " not found");
        }
    }

    @Override
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product with ID " + id + " not found");
        }
        productRepository.deleteById(id);
    }

    private ResponseProductDTO convertToDto(Product product) {
        return new ResponseProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getStock(),
                product.getPrice(),
                product.getCategory().getId(),
                product.getImages()
        );
    }
}
