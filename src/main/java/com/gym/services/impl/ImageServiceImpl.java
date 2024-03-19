package com.gym.services.impl;

import com.amazonaws.services.kms.model.NotFoundException;
import com.gym.dto.*;
import com.gym.dto.request.ImageRequestDTO;
import com.gym.dto.request.ImageS3RequestDTO;
import com.gym.dto.response.ImageResponseDTO;
import com.gym.dto.response.ProductResponseDTO;
import com.gym.entities.Category;
import com.gym.entities.Image;
import com.gym.entities.Product;
import com.gym.exceptions.DatabaseOperationException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.ImageRepository;
import com.gym.repositories.ProductRepository;
import com.gym.s3.services.StorageService;
import com.gym.services.CategoryService;
import com.gym.services.ImageService;
import com.gym.services.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final StorageService storageService;

    @Override
    public List<ImageResponseDTO> getAllImages() {
        return imageRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ImageResponseDTO getImageById(Long id) {
        return imageRepository.findById(id)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Image with ID " + id + " not found"));
    }

    @Override
    public ImageResponseDTO createImage(ImageRequestDTO imageRequestDTO) {
        try {
            if (imageRequestDTO.getProduct().getId() == null) {
                throw new IllegalArgumentException("Product ID in RequestImageDTO is null");
            }
            ProductResponseDTO productResponseDTO = productService.getProductById(imageRequestDTO.getProduct().getId());
            CategoryDTO categoryDTO = categoryService.getCategoryById(imageRequestDTO.getProduct().getCategory().getId());
            Category category = categoryDTO.categoryDTOToEntity(categoryDTO);
            Product product = productService.getProductById(imageRequestDTO.getProduct().getId()).responseProductDTOToEntity(productResponseDTO, category);
            Image image = Image.builder()
                    .title(imageRequestDTO.getTitle())
                    .url(imageRequestDTO.getUrl())
                    .product(imageRequestDTO.getProduct())
                    .build();
            Image savedImage = imageRepository.save(image);
            image.setId(savedImage.getId());
            return convertToResponseDto(image);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while saving image", e);
        }
    }

    @Override
    public ImageResponseDTO createImageS3(ImageS3RequestDTO imageS3RequestDTO) {
        try {
            if (imageS3RequestDTO.getProductId() == null) {
                throw new IllegalArgumentException("Product ID in RequestImageDTO is null");
            }

            Optional<Product> productOptional = productRepository.findByIdWithImages(imageS3RequestDTO.getProductId());
            if (productOptional.isEmpty()) {
                throw new NotFoundException("No se encontro el producto con ID: " + imageS3RequestDTO.getProductId());
            }
            Product product = productOptional.get();

            Image image = Image.builder()
                    .title(imageS3RequestDTO.getTitle())
                    .url(imageS3RequestDTO.getUrl())
                    .product(product)
                    .build();
            Image savedImage = imageRepository.save(image);

            product.addImage(savedImage);
            productRepository.save(product);

            image.setId(savedImage.getId());
            return convertToResponseDto(image);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while saving image", e);
        }
    }

    @Override
    public ImageResponseDTO updateImage(ImageRequestDTO imageRequestDTO) {
        Optional<Image> imageOptional = imageRepository.findById(imageRequestDTO.getId());
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();

            if (imageRequestDTO.getTitle() != null) {
                image.setTitle(imageRequestDTO.getTitle());
            }

            if (imageRequestDTO.getUrl() != null) {
                image.setUrl(imageRequestDTO.getUrl());
            }

            if (imageRequestDTO.getProduct() != null) {
                image.setProduct(imageRequestDTO.getProduct());
            }
            imageRepository.save(image);
            return convertToResponseDto(image);
        } else {
            throw new ResourceNotFoundException("Image with ID " + imageRequestDTO.getId() + " not found");
        }
    }

    @Override
    @Transactional
    public void deleteImageById(Long id) {
        if (!imageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Image with ID " + id + " not found");
        }
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image with ID " + id + " not found"));

        if (image.getProduct() != null) {
            // Desvincular la imagen del producto
            unlinkImageFromProduct(id);
        }
        storageService.deleteFile(image.getTitle());
        imageRepository.deleteById(id);
    }

    @Override
    public List<ImageResponseDTO> getImagesByProduct(Long productId){
        try {
            List<Image> productImages = imageRepository.findAllByProductId(productId);
            if (productImages.isEmpty()) {
                throw new NoSuchElementException("The product has no images");
            }
            return productImages.stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());
        } catch (DataAccessResourceFailureException e) {
            e.printStackTrace();
            throw new DataAccessResourceFailureException("Error accessing database resources", e);
        }
    }

    @Override
    public void unlinkImageFromProduct(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image with ID " + imageId + " not found"));

        Product product = image.getProduct();

        if (product != null) {
            product.removeImage(image);
            productRepository.save(product);
        }

        image.setProduct(null);
        imageRepository.save(image);
    }

    @Override
    public ImageResponseDTO convertToResponseDto(Image image) {
        return new ImageResponseDTO(
                image.getId(),
                image.getTitle(),
                image.getUrl(),
                image.getProduct().getId()
        );
    }

    @Override
    public ImageRequestDTO convertToRequestDto(Image image) {
        return new ImageRequestDTO(
                image.getId(),
                image.getTitle(),
                image.getUrl(),
                image.getProduct()
        );
    }
}
