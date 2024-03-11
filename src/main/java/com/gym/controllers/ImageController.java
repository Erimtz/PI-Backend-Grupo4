package com.gym.controllers;

import com.gym.dto.request.ImageRequestDTO;
import com.gym.dto.response.ImageResponseDTO;
import com.gym.entities.Image;
import com.gym.entities.Product;
import com.gym.repositories.ImageRepository;
import com.gym.repositories.ProductRepository;
import com.gym.services.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/image")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class ImageController {

    private final ImageService imageService;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public ImageController(ImageService imageService, ProductRepository productRepository, ImageRepository imageRepository) {
        this.imageService = imageService;
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
    }

    @Operation(summary = "Traer todas las imagenes")
    @GetMapping("/get-all")
    public ResponseEntity<List<ImageResponseDTO>> getAllImages() {
        List<ImageResponseDTO> imageDTO = imageService.getAllImages();
        return ResponseEntity.ok(imageDTO);
    }

    @Operation(summary = "Traer una imagen por ID")
    @GetMapping("/get/{id}")
    public ResponseEntity<ImageResponseDTO> getImageById(@PathVariable Long id) {
        ImageResponseDTO imageDTO = imageService.getImageById(id);
        return ResponseEntity.ok(imageDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Agregar una imagen")
    @PostMapping("/create")
    public ResponseEntity<ImageResponseDTO> createImage(@Valid @RequestBody ImageRequestDTO imageRequestDTO) {
        ImageResponseDTO imageDTO = imageService.createImage(imageRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(imageDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una imagen")
    @PutMapping("/update/{id}")
    public ResponseEntity<ImageResponseDTO> updateImage(@PathVariable Long id, @Valid @RequestBody ImageRequestDTO imageRequestDTO) {
        imageRequestDTO.setId(id);
        ImageResponseDTO imageDTO = imageService.updateImage(imageRequestDTO);
        return ResponseEntity.ok(imageDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar una imagen por ID")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteImageById(@PathVariable Long id) {
        imageService.deleteImageById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload/{productId}")
    public ResponseEntity<String> uploadImage(@PathVariable Long productId, @RequestParam("file") MultipartFile file) {
        try {
            if (productId == null) {
                return ResponseEntity.badRequest().body("Product ID cannot be null");
            }

            String uploadDir = "src/main/resources/static/images/";
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Optional<Product> productOptional = productRepository.findByIdWithImages(productId);
            if (productOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("Product with ID " + productId + " not found");
            }

            Product product = productOptional.get();

            Image image = new Image();
            image.setTitle(fileName);
            image.setUrl(uploadDir + fileName);
            image.setProduct(product);

            ImageRequestDTO imageRequestDTO = new ImageRequestDTO();
            imageRequestDTO.setTitle(image.getTitle());
            imageRequestDTO.setUrl(image.getUrl());
            imageRequestDTO.setProduct(image.getProduct());

            imageRepository.save(image);

            product.addImage(image);

            productRepository.save(product);

            return ResponseEntity.ok("Image uploaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }
}
