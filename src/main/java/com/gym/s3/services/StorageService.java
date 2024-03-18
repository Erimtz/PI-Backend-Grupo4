package com.gym.s3.services;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String uploadFile(MultipartFile file);
    public String getFileUrl(String fileName);
}
