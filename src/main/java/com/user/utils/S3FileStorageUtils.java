package com.user.utils;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class S3FileStorageUtils {
    private final AmazonS3 s3Client;
    private final AmazonS3 amazonS3;

    public S3FileStorageUtils(AmazonS3 s3Client, AmazonS3 amazonS3) {
        this.s3Client = s3Client;
        this.amazonS3 = amazonS3;
    }

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

//    upload to s3 bucket
    public String uploadFile(MultipartFile file) {
        try{
            String fileName = "users/" + file.getOriginalFilename();
            amazonS3.putObject(bucketName, fileName, file.getInputStream(), null);
            return amazonS3.getUrl(bucketName, fileName).toString();
        } catch (IOException e) {
            return "Error uploading file" + e.getMessage();
        }
    }
}
