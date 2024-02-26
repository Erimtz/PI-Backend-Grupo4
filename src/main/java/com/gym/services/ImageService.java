package com.gym.services;

import com.gym.entities.Image;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.ImageRepository;
import com.gym.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProductRepository productRepository;

    public ResponseEntity<List<Image>> getAllImage(){
        return ResponseEntity.ok((List<Image>) imageRepository.findAll());
    }

    public ResponseEntity<Image> getImage(Long id) throws ResourceNotFoundException {
        ResponseEntity<Image> response = null;
        Optional<Image> imageSearched = imageRepository.findById(id);
        if(imageSearched.isPresent()) {
            response = ResponseEntity.ok(imageSearched.get());
        } else {
            throw new ResourceNotFoundException("The image with id " + id + " has not been found.");
        }
        return response;
    }

    public Image saveImage(Long product_id, Image image){

        return imageRepository.save(image);
    }
}
