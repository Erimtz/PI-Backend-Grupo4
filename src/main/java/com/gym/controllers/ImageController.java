package com.gym.controllers;


import com.gym.dao.IImageDAO;
import com.gym.dto.ImageDTO;
import com.gym.entities.Image;
import com.gym.services.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Validated
@RestController
@RequestMapping("/image")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class ImageController {

    private final ImageService imageService;

    private IImageDAO imageDAO;

    public ImageController(ImageService imageService, IImageDAO imageDAO) {
        this.imageService = imageService;
        this.imageDAO = imageDAO;
    }

    @Operation(summary = "Traer todas las imagenes")
    @GetMapping("/get-all")
    public ResponseEntity<List<ImageDTO>> getAllImages() {
        List<ImageDTO> imageDTO = imageService.getAllImages();
        return ResponseEntity.ok(imageDTO);
    }

    @Operation(summary = "Traer una imagen por ID")
    @GetMapping("/get/{id}")
    public ResponseEntity<ImageDTO> getImageById(@PathVariable Long id) {
        ImageDTO imageDTO = imageService.getImageById(id);
        return ResponseEntity.ok(imageDTO);
    }

    @Operation(summary = "Agregar una imagen")
    @PostMapping("/create")
    public ResponseEntity<ImageDTO> createImage(@RequestBody ImageDTO imageDTO) {
        ImageDTO image = imageService.createImage(imageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(image);
    }

    @Operation(summary = "Actualizar una imagen")
    @PutMapping("/update/{id}")
    public ResponseEntity<ImageDTO> updateImage(@PathVariable Long id, @Valid @RequestBody ImageDTO imageDTO) {
        imageDTO.setId(id);
        ImageDTO image = imageService.updateImage(imageDTO);
        return ResponseEntity.ok(image);
    }

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
    public String savePhoto (@RequestParam(name = "file", required = false)MultipartFile url, Image image,
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
}
