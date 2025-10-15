package com.manawallpapers.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
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

            // Supabase upload endpoint
            String uploadUrl = String.format("%s/storage/v1/object/%s/%s",
                    supabaseS3Url.replace(".storage", ""),
                    bucketName,
                    objectKey);

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Bearer " + supabaseKey);
            headers.set("apikey", supabaseKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String publicUrl = String.format(
                        "%s/storage/v1/object/public/%s/%s",
                        supabaseS3Url.replace(".storage", ""),
                        bucketName,
                        objectKey
                );
                log.info("File uploaded successfully: {}", publicUrl);
                return publicUrl;
            } else {
                throw new RuntimeException("Failed to upload: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error uploading file to Supabase", e);
            throw new RuntimeException("File upload failed", e);
        }
    }
}
