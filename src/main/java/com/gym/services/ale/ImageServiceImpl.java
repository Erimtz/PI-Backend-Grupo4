package com.gym.services.ale;

import com.gym.dto.RequestImageDTO;
import com.gym.dto.ResponseImageDTO;
import com.gym.entities.Image;
import com.gym.exceptions.DatabaseOperationException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    private final ImageRepository imageRepository;

    @Override
    public List<ResponseImageDTO> getAllImages() {
        return imageRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseImageDTO getImageById(Long id) {
        return imageRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Image with ID " + id + " not found"));
    }

    @Override
    public ResponseImageDTO createImage(RequestImageDTO requestImageDTO) {
        try {
            Image image = Image.builder()
                    .title(requestImageDTO.getTitle())
                    .url(requestImageDTO.getUrl())
                    .product(requestImageDTO.getProduct())
                    .build();
            imageRepository.save(image);
            return convertToDto(image);
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
            return convertToDto(image);
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

    private ResponseImageDTO convertToDto(Image image) {
        return new ResponseImageDTO(
                image.getId(),
                image.getTitle(),
                image.getUrl(),
                image.getProduct().getId()
        );
    }
}
