package com.gym.services;

import com.gym.dto.ImageDTO;
import com.gym.entities.Image;
import com.gym.exceptions.DatabaseOperationException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ImageServiceImpl  implements ImageService {

    private ImageRepository imageRepository;

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public List<ImageDTO> getAllImages() {
        return imageRepository.findAll()
                .stream()
                .map(Image::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ImageDTO getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image with ID " + id + " not found")).toDto();
    }

    @Override
    public ImageDTO createImage(ImageDTO imageDTO) {
        try {
            Image image = Image.builder()
                    .title(imageDTO.getTitle())
                    .foto(imageDTO.getFoto())
                    .product(imageDTO.getProduct())
                    .build();
            imageRepository.save(image);
            return image.toDto();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while saving image", e);
        }
    }



    @Override
    public ImageDTO updateImage(ImageDTO imageDTO) {
        Optional<Image> imageOptional = imageRepository.findById(imageDTO.getId());
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();

            if (imageDTO.getTitle() != null) {
                image.setTitle(imageDTO.getTitle());
            }

            if (imageDTO.getFoto() != null) {
                image.setFoto(imageDTO.getFoto());
            }

            if (imageDTO.getProduct() != null) {
                image.setProduct(imageDTO.getProduct());
            }
            imageRepository.save(image);
            return image.toDto();
        } else {
            throw new ResourceNotFoundException("Image with ID " + imageDTO.getId() + " not found");
        }
    }

    @Override
    public void deleteImageById(Long id) {
        if (!imageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Image with ID " + id + " not found");
        }
        imageRepository.deleteById(id);
    }

}
