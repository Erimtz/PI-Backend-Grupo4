package com.gym.services;

import com.gym.dto.ImageDTO;
import com.gym.dto.ProductDTO;
import com.gym.dto.RequestProductDTO;
import com.gym.dto.ResponseProductDTO;
import com.gym.entities.Category;
import com.gym.entities.Image;
import com.gym.entities.Product;
import com.gym.exceptions.NotEnoughStockException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.CategoryRepository;
import com.gym.repositories.ImageRepository;
import com.gym.repositories.ProductRepository;
import com.gym.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

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
        Product productToUpdate = productRepository.findById(requestProductDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("The product with id " + requestProductDTO.getId() + " has not been found to be updated."));

        if (requestProductDTO.getName() != null) {
            productToUpdate.setName(requestProductDTO.getName());
        }
        if (requestProductDTO.getDescription() != null) {
            productToUpdate.setDescription(requestProductDTO.getDescription());
        }
        if (requestProductDTO.getStock() != null) {
            productToUpdate.setStock(requestProductDTO.getStock());
        }
        if (requestProductDTO.getPrice() != null) {
            productToUpdate.setPrice(requestProductDTO.getPrice());
        }
        if (requestProductDTO.getCategoryId() != null) {
            productToUpdate.setCategory(categoryRepository.findById(requestProductDTO.getCategoryId()).get());
        }
        Product savedProduct = productRepository.save(productToUpdate);
        return convertToDto(savedProduct);
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

    @Override
    public ProductDTO getProductsByCategory(Long category_id) throws ResourceNotFoundException {

        List<Product> productList = null;
        List<ProductDTO> productsDTOList;

        Optional<Category> searchedCategory = categoryRepository.findById(category_id);
        if (searchedCategory.isPresent()){
            productList =productRepository.getProductsByCategory(category_id);
        }else {
            throw new ResourceNotFoundException("The category with id " + category_id + " has not been found.");
        }

        productsDTOList= productList.stream().map(p -> p.toDto()).collect(Collectors.toList());
        return (ProductDTO) productsDTOList;

    }

    @Override
    public List<ProductDTO> getProductsByName(String name) {
        List<Product> productList = productRepository.findByName(name);
        return productList.stream().map(Product::toDto).collect(Collectors.toList());
    }
    @Override
    public List<ProductDTO> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        List<Product> productList = productRepository.findByPriceBetween(minPrice, maxPrice);
        return productList.stream().map(Product::toDto).collect(Collectors.toList());
    }
    @Override
    public List<ProductDTO> getAllProductSortedByPriceAsc() {
        List<Product> productList = productRepository.findAll();
        productList.sort(Comparator.comparing(Product::getPrice));
        return productList.stream().map(Product::toDto).collect(Collectors.toList());
    }
    @Override
    public List<ProductDTO> getAllProductSortedByPriceDesc() {
        List<Product> productList = productRepository.findAll();
        productList.sort(Comparator.comparing(Product::getPrice).reversed());
        return productList.stream().map(Product::toDto).collect(Collectors.toList());
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
