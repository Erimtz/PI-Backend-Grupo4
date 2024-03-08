package com.gym.services.impl;

import com.gym.dto.*;
import com.gym.dto.request.ImageRequestDTO;
import com.gym.dto.response.ImageResponseDTO;
import com.gym.dto.response.ProductResponseDTO;
import com.gym.entities.Category;
import com.gym.entities.Image;
import com.gym.entities.Product;
import com.gym.exceptions.DatabaseOperationException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.ImageRepository;
import com.gym.services.CategoryService;
import com.gym.services.ImageService;
import com.gym.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ProductService productService;
    private final CategoryService categoryService;

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
    public void deleteImageById(Long id) {
        if (!imageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Image with ID " + id + " not found");
        }
        imageRepository.deleteById(id);
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
