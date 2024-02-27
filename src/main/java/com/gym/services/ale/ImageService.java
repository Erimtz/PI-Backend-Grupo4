package com.gym.services.ale;

import com.gym.dto.*;

import java.util.List;

public interface ImageService {

    List<ResponseImageDTO> getAllImages();
    ResponseImageDTO getImageById(Long id);
    ResponseImageDTO createImage(RequestImageDTO requestImageDTO);
    ResponseImageDTO updateImage(RequestImageDTO requestImageDTO);
    void deleteImageById(Long id);

}
