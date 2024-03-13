package com.gym.controllers;

import com.gym.dto.request.ImageRequestDTO;
import com.gym.dto.response.ImageResponseDTO;
import com.gym.entities.Image;
import com.gym.entities.Product;
import com.gym.repositories.ImageRepository;
import com.gym.repositories.ProductRepository;
//import com.gym.services.AWSS3Service;
import com.gym.services.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/image")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class ImageController {

    private final ImageService imageService;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
//    private final AWSS3Service awss3Service;

    @Autowired
    public ImageController(ImageService imageService, ProductRepository productRepository, ImageRepository imageRepository/*, AWSS3Service awss3Service*/) {
        this.imageService = imageService;
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
//        this.awss3Service = awss3Service;
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

    @GetMapping("/products/{productId}")
    public ResponseEntity<List<ImageResponseDTO>> getImagesByProduct(@PathVariable Long productId) {
        try {
            List<ImageResponseDTO> images = imageService.getImagesByProduct(productId);
            return ResponseEntity.ok(images);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

//    @PostMapping("/uploadS3/{productId}")
//    public ResponseEntity<String> uploadImageS3(@PathVariable Long productId, @RequestParam("file") MultipartFile file) {
//        try {
//            if (productId == null) {
//                return ResponseEntity.badRequest().body("Product ID cannot be null");
//            }
//
//            if (file.isEmpty()) {
//                return ResponseEntity.badRequest().body("File is empty");
//            }
//
//            String s3FileName = awss3Service.uploadImage(file);
//
//
//            String s3Url = "URL del archivo en S3";
//
//            Optional<Product> productOptional = productRepository.findByIdWithImages(productId);
//            if (productOptional.isEmpty()) {
//                return ResponseEntity.badRequest().body("Product with ID " + productId + " not found");
//            }
//            Product product = productOptional.get();
//
//            Image image = new Image();
//            image.setTitle(s3FileName);
//            image.setUrl(s3Url);
//            image.setProduct(product);
//
//            ImageResponseDTO savedImageDTO = imageService.createImage(imageService.convertToRequestDto(image));
//
//            Image savedImage = new Image();
//            savedImage.setId(savedImageDTO.getId());
//            savedImage.setTitle(savedImageDTO.getTitle());
//            savedImage.setUrl(savedImageDTO.getUrl());
//            savedImage.setProduct(productRepository.findById(savedImageDTO.getProductId()).get());
//
//            product.addImage(savedImage);
//            productRepository.save(product);
//
//            return ResponseEntity.ok("Image uploaded successfully");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
//        }
//    }
}
