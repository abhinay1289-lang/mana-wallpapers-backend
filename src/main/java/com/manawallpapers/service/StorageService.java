package com.manawallpapers.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class StorageService {

    @Value("${supabase.url}")
    private String supabaseS3Url;

    @Value("${supabase.key}")
    private String supabaseKey; // service role key

    @Value("${supabase.storage.bucket-name}")
    private String bucketName;

    private S3Client buildS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create("supabase", supabaseKey);
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1) // dummy region; Supabase ignores it
                .endpointOverride(URI.create(supabaseS3Url))
                .forcePathStyle(true)
                .build();
    }

    /**
     * Uploads an image to Supabase Storage (S3-compatible API)
     * Example folderPath: "2d-wallpapers/Illustration & Art/Anime"
     */
    public String uploadFile(MultipartFile file, String folderPath) {
        try {
            String uniqueName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String objectKey = folderPath + "/" + uniqueName;

            S3Client s3 = buildS3Client();

            try (InputStream inputStream = file.getInputStream()) {
                PutObjectRequest putRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .contentType(file.getContentType())
                        .build();

                s3.putObject(putRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, file.getSize()));
            }

            // Construct a public-style URL (if your bucket is public)
            String publicUrl = supabaseS3Url.replace("/s3", "") +
                    "/object/public/" + bucketName + "/" + objectKey;

            log.info("File uploaded successfully: {}", publicUrl);
            return publicUrl;

        } catch (Exception e) {
            log.error("Error uploading file to Supabase S3", e);
            throw new RuntimeException("File upload failed", e);
        }
    }
}
