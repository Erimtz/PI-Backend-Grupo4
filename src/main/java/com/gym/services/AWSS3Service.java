//package com.gym.services;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.List;
//
//import org.springframework.web.multipart.MultipartFile;
//
//public interface AWSS3Service {
//
//    void uploadFile(MultipartFile file);
//
//    String uploadImage(MultipartFile file);
//
//    List<String> getObjectsFromS3();
//
//    InputStream downloadFile(String key);
//}