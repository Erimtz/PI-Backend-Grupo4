package com.gym.controllers.ale;

import com.gym.dto.*;
import com.gym.services.ale.ImageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ResponseImageDTO>> getAllImages() {
        List<ResponseImageDTO> imageDTO = imageService.getAllImages();
        return ResponseEntity.ok(imageDTO);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseImageDTO> getImageById(@PathVariable Long id) {
        ResponseImageDTO imageDTO = imageService.getImageById(id);
        return ResponseEntity.ok(imageDTO);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseImageDTO> createImage(@Valid @RequestBody RequestImageDTO requestImageDTO) {
        ResponseImageDTO imageDTO = imageService.createImage(requestImageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(imageDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseImageDTO> updateImage(@PathVariable Long id, @Valid @RequestBody RequestImageDTO requestImageDTO) {
        requestImageDTO.setId(id);
        ResponseImageDTO imageDTO = imageService.updateImage(requestImageDTO);
        return ResponseEntity.ok(imageDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteImageById(@PathVariable Long id) {
        imageService.deleteImageById(id);
        return ResponseEntity.noContent().build();
    }
}
