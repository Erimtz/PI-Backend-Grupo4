package com.gym.services;

import com.gym.dto.request.ImageRequestDTO;
import com.gym.dto.response.ImageResponseDTO;
import com.gym.entities.Image;

import java.util.List;

public interface ImageService {

    List<ImageResponseDTO> getAllImages();
    ImageResponseDTO getImageById(Long id);
    ImageResponseDTO createImage(ImageRequestDTO imageRequestDTO);
    ImageResponseDTO updateImage(ImageRequestDTO imageRequestDTO);
    void deleteImageById(Long id);
    ImageResponseDTO convertToResponseDto(Image image);
    ImageRequestDTO convertToRequestDto(Image image);

}
