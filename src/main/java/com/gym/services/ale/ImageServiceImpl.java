package com.gym.services.ale;

import com.gym.dto.*;
import com.gym.entities.Category;
import com.gym.entities.Image;
import com.gym.entities.Product;
import com.gym.exceptions.DatabaseOperationException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.ImageRepository;
import com.gym.services.CategoryService;
import com.gym.services.ale.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    private final ImageRepository imageRepository;
    private final ProductService productService;
    private final CategoryService categoryService;

    @Override
    public List<ResponseImageDTO> getAllImages() {
        return imageRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseImageDTO getImageById(Long id) {
        return imageRepository.findById(id)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Image with ID " + id + " not found"));
    }

    @Override
    public ResponseImageDTO createImage(RequestImageDTO requestImageDTO) {
        try {
            if (requestImageDTO.getProduct().getId() == null) {
                throw new IllegalArgumentException("Product ID in RequestImageDTO is null");
            }
            ResponseProductDTO responseProductDTO = productService.getProductById(requestImageDTO.getProduct().getId());
            CategoryDTO categoryDTO = categoryService.getCategoryById(requestImageDTO.getProduct().getCategory().getId());
            Category category = categoryDTO.categoryDTOToEntity(categoryDTO);
            Product product = productService.getProductById(requestImageDTO.getProduct().getId()).responseProductDTOToEntity(responseProductDTO, category);
            Image image = Image.builder()
                    .title(requestImageDTO.getTitle())
                    .url(requestImageDTO.getUrl())
                    .product(requestImageDTO.getProduct())
                    .build();
            Image savedImage = imageRepository.save(image);
            image.setId(savedImage.getId());
            return convertToResponseDto(image);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while saving image", e);
        }

    }

    @Override
    public ResponseImageDTO updateImage(RequestImageDTO requestImageDTO) {
        Optional<Image> imageOptional = imageRepository.findById(requestImageDTO.getId());
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();

            if (requestImageDTO.getTitle() != null) {
                image.setTitle(requestImageDTO.getTitle());
            }

            if (requestImageDTO.getUrl() != null) {
                image.setUrl(requestImageDTO.getUrl());
            }

            if (requestImageDTO.getProduct() != null) {
                image.setProduct(requestImageDTO.getProduct());
            }
            imageRepository.save(image);
            return convertToResponseDto(image);
        } else {
            throw new ResourceNotFoundException("Image with ID " + requestImageDTO.getId() + " not found");
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
    public ResponseImageDTO convertToResponseDto(Image image) {
        return new ResponseImageDTO(
                image.getId(),
                image.getTitle(),
                image.getUrl(),
                image.getProduct().getId()
        );
    }

    @Override
    public RequestImageDTO convertToRequestDto(Image image) {
        return new RequestImageDTO(
                image.getId(),
                image.getTitle(),
                image.getUrl(),
                image.getProduct()
        );
    }
}
