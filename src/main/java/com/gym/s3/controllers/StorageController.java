package com.gym.s3.controllers;

import com.gym.entities.Purchase;
import com.gym.s3.services.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/storage/")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @Operation(summary = "Subir archivo a bucket S3 de AWS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo subido exitosamente", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
    })
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file) {
        storageService.uploadFile(file);
        return ResponseEntity.ok("File uploaded successfully");
    }

    @Operation(summary = "Eliminar archivo de bucket S3 de AWS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo eliminado exitosamente", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
    })
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam(value = "fileName") String fileName) {
        storageService.deleteFile(fileName);
        return ResponseEntity.ok("File deleted successfully");
    }
}
