package com.gym.controllers.ale;

import com.gym.dao.IImageDAO;
import com.gym.dto.*;
import com.gym.entities.Category;
import com.gym.entities.Image;
import com.gym.entities.Product;
import com.gym.services.CategoryService;
import com.gym.services.ale.ImageService;
import com.gym.services.ale.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Validated
@RestController
@RequestMapping("/image")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class ImageController {

    private final ImageService imageService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final IImageDAO imageDAO;

    @Autowired
    public ImageController(ImageService imageService, IImageDAO imageDAO, ProductService productService, CategoryService categoryService) {
        this.imageService = imageService;
        this.imageDAO = imageDAO;
        this.productService = productService;
        this.categoryService = categoryService;
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

    @GetMapping("/")
    public String form (Model model) {
        model.addAttribute("image", new Image());
        return "form";
    }
    @PostMapping("/save")
    public String savePhoto (@RequestParam(name = "file", required = false) MultipartFile url, Image image,
                             RedirectAttributes flash) {
        if (!url.isEmpty()) {
            String ruta = "C://descargas";
            try {
                byte[] bytes = url.getBytes();
                Path rutaAbsoluta = Paths.get(ruta + "//" + url.getOriginalFilename());
                Files.write(rutaAbsoluta, bytes);
                image.setUrl(url.getOriginalFilename());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            imageDAO.save(image);
            flash.addAttribute("success", "Foto subida");
        }
        return "redirect:/";
    }

    @GetMapping("/list")
    public String tolist(Model model) {
        model.addAttribute("image", imageDAO.findAll());
        return "list";
    }

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

            ResponseProductDTO responseProductDTO = productService.getProductById(productId);

            if (responseProductDTO == null) {
                return ResponseEntity.badRequest().body("Product with ID " + productId + " not found");
            }

            CategoryDTO categoryDTO = categoryService.getCategoryById(responseProductDTO.getCategoryId());
            Category category = categoryDTO.categoryDTOToEntity(categoryDTO);
            Product product = responseProductDTO.responseProductDTOToEntity(responseProductDTO, category);
            Image image = new Image();
            image.setTitle(fileName);
            image.setUrl(uploadDir + fileName);
            image.setProduct(product);
            RequestImageDTO requestImageDTO = imageService.convertToRequestDto(image);
            requestImageDTO.setProductId(productId);
            imageService.createImage(requestImageDTO);

            return ResponseEntity.ok("Image uploaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }

    }
}
