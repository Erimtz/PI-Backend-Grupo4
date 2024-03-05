package com.gym.controllers.ale;

import com.gym.dto.*;
import com.gym.services.ale.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/image")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Operation(summary = "Traer todas las imagenes")
    @GetMapping("/get-all")
    public ResponseEntity<List<ResponseImageDTO>> getAllImages() {
        List<ResponseImageDTO> imageDTO = imageService.getAllImages();
        return ResponseEntity.ok(imageDTO);
    }

    @Operation(summary = "Traer una imagen por ID")
    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseImageDTO> getImageById(@PathVariable Long id) {
        ResponseImageDTO imageDTO = imageService.getImageById(id);
        return ResponseEntity.ok(imageDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Agregar una imagen")
    @PostMapping("/create")
    public ResponseEntity<ResponseImageDTO> createImage(@Valid @RequestBody RequestImageDTO requestImageDTO) {
        ResponseImageDTO imageDTO = imageService.createImage(requestImageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(imageDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una imagen")
    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseImageDTO> updateImage(@PathVariable Long id, @Valid @RequestBody RequestImageDTO requestImageDTO) {
        requestImageDTO.setId(id);
        ResponseImageDTO imageDTO = imageService.updateImage(requestImageDTO);
        return ResponseEntity.ok(imageDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar una imagen por ID")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteImageById(@PathVariable Long id) {
        imageService.deleteImageById(id);
        return ResponseEntity.noContent().build();
    }
}
