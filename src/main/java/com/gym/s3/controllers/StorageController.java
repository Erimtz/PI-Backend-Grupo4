package com.gym.s3.controllers;

//import com.gym.s3.services.StorageService;
import com.gym.s3.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/storage/")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam(value = "file") MultipartFile file) {
        return storageService.uploadFile(file);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam(value = "fileName") String fileName) {
        storageService.deleteFile(fileName);
        return ResponseEntity.ok("File deleted successfully");
    }
}
