package com.gym.services.impl;

import com.gym.configuration.LevenshteinDistance;
import com.gym.dto.request.ProductFiltersRequestDTO;
import com.gym.dto.request.ProductRequestDTO;
import com.gym.dto.response.ImageResponseDTO;
import com.gym.dto.response.ProductResponseDTO;
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
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
    }

    public Optional<ProductResponseDTO> getProductByIdWithImages(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Optional<Category> categoryOptional = categoryRepository.findById(productRequestDTO.getCategoryId());
        if (categoryOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product with ID " + productRequestDTO.getCategoryId() + " not found");
        }
        Category category = categoryOptional.get();

        Set<Image> images = new HashSet<>();

        Product product = new Product();
        product.setName(productRequestDTO.getName());
        product.setPrice(productRequestDTO.getPrice());
        product.setDescription(productRequestDTO.getDescription());
        product.setStock(productRequestDTO.getStock());
        product.setCategory(category);
        product.setImages(images);

        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    @Override
    public ProductResponseDTO updateProduct(ProductRequestDTO productRequestDTO) {
        Product productToUpdate = productRepository.findById(productRequestDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("The product with id " + productRequestDTO.getId() + " has not been found to be updated."));

        if (productRequestDTO.getName() != null) {
            productToUpdate.setName(productRequestDTO.getName());
        }
        if (productRequestDTO.getDescription() != null) {
            productToUpdate.setDescription(productRequestDTO.getDescription());
        }
        if (productRequestDTO.getStock() != null) {
            productToUpdate.setStock(productRequestDTO.getStock());
        }
        if (productRequestDTO.getPrice() != null) {
            productToUpdate.setPrice(productRequestDTO.getPrice());
        }
        if (productRequestDTO.getCategoryId() != null) {
            productToUpdate.setCategory(categoryRepository.findById(productRequestDTO.getCategoryId()).get());
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
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product with ID " + id + " not found");
        }
        Product product = productOptional.get();
        Set<Image> images = product.getImages();

        if (!images.isEmpty()) {
            imageRepository.deleteAll(images);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductResponseDTO> getProductsByCategory(Long categoryId) throws ResourceNotFoundException {
        List<Product> products = productRepository.getProductsByCategory(categoryId);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron productos para la categoría con ID: " + categoryId);
        }
        return convertToDtoList(products);
    }

    @Override
    public List<ProductResponseDTO> findProductsByCategoryAndFilters(Long categoryId, ProductFiltersRequestDTO request, String orderBy, String orderDirection) {
        if (categoryId == null) {
            throw new IllegalArgumentException("El ID de categoría no puede ser nulo.");
        }
        List<Product> products = productRepository.findProductsByCategoryAndFilters(
                categoryId,
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getHasStock(),
                orderBy,
                orderDirection
        );
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDTO> searchProductsByName(String searchTerm) {
        List<Product> searchedProducts = productRepository.findProductsByName(searchTerm);
        List<Product> sortedProducts = searchedProducts.stream()
                .sorted(Comparator.comparingInt(product -> LevenshteinDistance.calculateDistance(searchTerm, product.getName())))
                .collect(Collectors.toList());
        List<ProductResponseDTO> productResponseDTOs = sortedProducts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return productResponseDTOs;
    }

    @Override
    public List<ProductResponseDTO> searchProductsByNameAndFilters(String searchTerm, ProductFiltersRequestDTO request, String orderBy, String orderDirection) {
        List<Product> products = productRepository.findProductsByNameAndFilters(
                searchTerm,
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getHasStock(),
                orderBy,
                orderDirection
        );
        List<Product> sortedProducts;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            sortedProducts = products.stream()
                    .sorted(Comparator.comparingInt(product -> LevenshteinDistance.calculateDistance(searchTerm, product.getName())))
                    .collect(Collectors.toList());
        } else {
            sortedProducts = products;
        }
        return sortedProducts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDTO convertToDto(Product product) {
        Set<ImageResponseDTO> imageResponseDTOs = product.getImages().stream()
                .map(image -> {
                    ImageResponseDTO imageResponseDTO = new ImageResponseDTO();
                    imageResponseDTO.setId(image.getId());
                    imageResponseDTO.setTitle(image.getTitle());
                    imageResponseDTO.setUrl(image.getUrl());
                    return imageResponseDTO;
                })
                .collect(Collectors.toSet());

        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(product.getId());
        productResponseDTO.setName(product.getName());
        productResponseDTO.setDescription(product.getDescription());
        productResponseDTO.setStock(product.getStock());
        productResponseDTO.setPrice(product.getPrice());
        productResponseDTO.setCategoryId(product.getCategory().getId());
        productResponseDTO.setImages(imageResponseDTOs);

        return productResponseDTO;
    }

    public List<ProductResponseDTO> convertToDtoList(List<Product> productList) {
        return productList.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private ImageResponseDTO convertImageToDto(Image image) {
        return new ImageResponseDTO(
                image.getId(),
                image.getTitle(),
                image.getUrl(),
                image.getProduct().getId()
        );
    }
}
