package com.ecommerce.store.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service 
@RequiredArgsConstructor 
public class ImageUploaderService {
    
    private final S3Client s3Client;

    @Value ("${minio.bucket-name}")
    private String bucketName;

    @Value ("${minio.external-url}")
    private String minioUrl;

    @PostConstruct 
    public void init() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            System.out.println("S3 Bucket '" + bucketName + "' found.");

        }   catch (NoSuchBucketException exception) {
            System.out.println("S3 Bucket not found. Creating '" + bucketName + "'...");
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        }   catch (Exception exception) {
            System.err.println(exception.getMessage());
        }
    }

    public String uploadImage(MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                                .bucket(bucketName)
                                                                .key(fileName)
                                                                .contentType(file.getContentType())
                                                                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return minioUrl + "/" + bucketName + "/" + fileName;
        }   catch (IOException exception) {
            throw new RuntimeException("Failed to upload the image to S3", exception);
        }
    }
}