package com.gym.services;

import com.gym.dto.ImageDTO;
import com.gym.dto.ProductDTO;
import com.gym.entities.Category;
import com.gym.entities.Image;
import com.gym.entities.Product;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.CategoryRepository;
import com.gym.repositories.ImageRepository;
import com.gym.repositories.ProductRepository;
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
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(Product::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(Product::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + id + " not found"));
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

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        Optional<Category> categoryOptional = categoryRepository.findById(productDTO.getCategory().getId());
        if (categoryOptional.isEmpty()) {
            throw new ResourceNotFoundException("Category with ID " + productDTO.getCategory().getId() + " not found");
        }
        Category category = categoryOptional.get();

        Set<Image> images = new HashSet<>();

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setStock(productDTO.getStock());
        product.setCategory(category);
        product.setImages(images);

        Product savedProduct = productRepository.save(product);
        return savedProduct.toDto();
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO) {
        Product productToUpdate = productRepository.findById(productDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("The product with id " + productDTO + " has not been found to be updated."));

        if (productDTO.getName() != null) {
            productToUpdate.setName(productDTO.getName());
        }
        if (productDTO.getDescription() != null) {
            productToUpdate.setDescription(productDTO.getDescription());
        }
        if (productDTO.getStock() != null) {
            productToUpdate.setStock(productDTO.getStock());
        }
        if (productDTO.getPrice() != null) {
            productToUpdate.setPrice(productDTO.getPrice());
        }
        if (productDTO.getPurchase() != null) {
            productToUpdate.setPurchase(productDTO.getPurchase());
        }
        if (productDTO.getCategory() != null) {
            productToUpdate.setCategory(productDTO.getCategory());
        }
        if (productDTO.getImages() != null) {
            Set<Image> imageSet = productDTO.getImages().stream()
                    .map(ImageDTO::toEntity)
                    .collect(Collectors.toSet());
            productToUpdate.setImages(imageSet);
        }
        return productRepository.save(productToUpdate).toDto();
    }

    @Override
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product with ID " + id + " not found");
        }
        productRepository.deleteById(id);
    }
}
