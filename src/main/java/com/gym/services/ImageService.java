package com.gym.services;

import com.gym.dto.*;
import com.gym.entities.Image;

import java.util.List;

public interface ImageService {

    List<ResponseImageDTO> getAllImages();
    ResponseImageDTO getImageById(Long id);
    ResponseImageDTO createImage(RequestImageDTO requestImageDTO);
    ResponseImageDTO updateImage(RequestImageDTO requestImageDTO);
    void deleteImageById(Long id);
    ResponseImageDTO convertToResponseDto(Image image);
    RequestImageDTO convertToRequestDto(Image image);

}
