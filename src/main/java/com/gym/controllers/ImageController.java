package com.gym.controllers;

import com.gym.dto.request.ImageRequestDTO;
import com.gym.dto.request.ImageS3RequestDTO;
import com.gym.dto.response.ImageResponseDTO;
import com.gym.entities.Image;
import com.gym.entities.Product;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.ImageRepository;
import com.gym.repositories.ProductRepository;
//import com.gym.services.AWSS3Service;
//import com.gym.s3.services.StorageService;
import com.gym.s3.services.StorageService;
import com.gym.services.ImageService;
import com.gym.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class ImageController {

    private final ImageService imageService;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ImageRepository imageRepository;
    private final StorageService storageService;

    @Operation(summary = "Get all images")
    @GetMapping("/get-all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Images retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Image.class))
            })
    })
    public ResponseEntity<List<ImageResponseDTO>> getAllImages() {
        List<ImageResponseDTO> imageDTO = imageService.getAllImages();
        return ResponseEntity.ok(imageDTO);
    }

    @Operation(summary = "Get an image by ID")
    @GetMapping("/get/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Image.class))
            })
    })
    public ResponseEntity<ImageResponseDTO> getImageById(@PathVariable Long id) {
        ImageResponseDTO imageDTO = imageService.getImageById(id);
        return ResponseEntity.ok(imageDTO);
    }

    @Operation(summary = "Get images by product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Images retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Image.class))
            }),
            @ApiResponse(responseCode = "500", description = "An error occurred while processing the request",content =
            @Content),
            @ApiResponse(responseCode = "404", description = "Product not found",content =
            @Content)
    })
    @GetMapping("/product/{productId}")
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
    @Operation(summary = "Add an image")
    @PostMapping("/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image created successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Image.class))
            })
    })
    public ResponseEntity<ImageResponseDTO> createImage(@Valid @RequestBody ImageRequestDTO imageRequestDTO) {
        ImageResponseDTO imageDTO = imageService.createImage(imageRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(imageDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an image")
    @PutMapping("/update/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image updated successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Image.class))
            })
    })
    public ResponseEntity<ImageResponseDTO> updateImage(@PathVariable Long id, @Valid @RequestBody ImageRequestDTO imageRequestDTO) {
        imageRequestDTO.setId(id);
        ImageResponseDTO imageDTO = imageService.updateImage(imageRequestDTO);
        return ResponseEntity.ok(imageDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an image by ID")
    @DeleteMapping("/delete/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Image deleted successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Image.class))
            }),
            @ApiResponse(responseCode = "500", description = "An error occurred while processing the request",content =
            @Content),
            @ApiResponse(responseCode = "404", description = "Image not found",content =
            @Content)
    })
    public ResponseEntity<Void> deleteImageById(@PathVariable Long id) {
        try {
            ImageResponseDTO imageDTO = imageService.getImageById(id);
            if (imageDTO == null) {
                throw new ResourceNotFoundException("Image with ID " + id + " not found");
            }

            // Unlink the image from the product if it has a valid product ID
            Long productId = imageDTO.getProductId();
            if (productId != null) {
                imageService.unlinkImageFromProduct(id); // Update the product-image relationship
            }

            // Delete the image
            imageService.deleteImageById(id);

            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upload image to local repository")
    @PostMapping("/upload/{productId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Image.class))
            }),
            @ApiResponse(responseCode = "500", description = "The server cannot process the request",content =
            @Content),
            @ApiResponse(responseCode = "400", description = "An error occurred while processing the request",content =
            @Content),
    })
    @Transactional
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload/s3/{productId}")
    @Operation(summary = "Upload image to S3 bucket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Image.class))
            }),
            @ApiResponse(responseCode = "500", description = "The server cannot process the request",content =
            @Content),
            @ApiResponse(responseCode = "400", description = "An error occurred while processing the request",content =
            @Content),
    })
    public ResponseEntity<String> uploadImageS3(@PathVariable Long productId, @RequestParam("file") MultipartFile file) {
        try {
            if (productId == null) {
                return ResponseEntity.badRequest().body("Product ID cannot be null");
            }
            String uploadedFileName = storageService.uploadFile(file);

            ImageS3RequestDTO imageRequestDTO = new ImageS3RequestDTO();
            imageRequestDTO.setTitle(uploadedFileName);
            imageRequestDTO.setUrl(storageService.getFileUrl(uploadedFileName));
            imageRequestDTO.setProductId(productId);

            ImageResponseDTO imageResponseDTO = imageService.createImageS3(imageRequestDTO);

            return ResponseEntity.ok("Image uploaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }
}
