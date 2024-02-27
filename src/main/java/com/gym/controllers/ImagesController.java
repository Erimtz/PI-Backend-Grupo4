//package com.gym.controllers;
//
//import com.gym.entities.Image;
//import com.gym.exceptions.ResourceNotFoundException;
//import com.gym.services.ImageService;
//import io.swagger.v3.oas.annotations.Operation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/images")
//@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
//public class ImagesController {
//    @Autowired
//    private ImageService imageService;
//
//    @Operation(summary = "Traer todas las imagenes")
//    @GetMapping
//    public ResponseEntity<List<Image>> getAllImages(){
//        return imageService.getAllImage();
//    }
//
//    @Operation(summary = "Traer imagen por ID")
//    @GetMapping("/{id}")
//    public ResponseEntity<Image> getImage(@PathVariable Long id) throws ResourceNotFoundException {
//        return imageService.getImage(id);
//    }
//}
