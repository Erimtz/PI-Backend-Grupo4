package com.gym.services;

import com.gym.dto.ImageDTO;

import java.util.List;

public interface ImageService {
    List<ImageDTO> getAllImages();
    ImageDTO getImageById(Long id);
    ImageDTO createImage(ImageDTO imageDTO);
    ImageDTO updateImage(ImageDTO imageDTO);
    void deleteImageById(Long id);
}
