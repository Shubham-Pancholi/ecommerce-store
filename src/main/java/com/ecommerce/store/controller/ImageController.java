package com.ecommerce.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.store.service.ImageUploaderService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/api/v1/images")
@RequiredArgsConstructor 
public class ImageController {
    //
    private final ImageUploaderService imageUploadService;

    @PostMapping ("/upload")
    public ResponseEntity<String> uploadImage(
        @RequestParam ("file") MultipartFile file
    ) {
        String imageUrl = imageUploadService.uploadImage(file);
        return ResponseEntity.ok(imageUrl);
    }
}